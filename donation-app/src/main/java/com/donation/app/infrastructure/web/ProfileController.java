package com.donation.app.infrastructure.web;

import com.donation.app.domain.User;
import com.donation.app.infrastructure.web.api.ProfileApi;
import com.donation.app.infrastructure.web.dto.UpdateProfileRequest;
import com.donation.app.infrastructure.web.dto.UserProfileResponse;
import com.donation.app.usecase.GetProfileUseCase;
import com.donation.app.usecase.UpdateProfileUseCase;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Tag(name = "Профиль", description = "Управление профильными данными пользователя")
@SecurityRequirement(name = "BearerAuth")
public class ProfileController implements ProfileApi {

    private final GetProfileUseCase getProfileUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;

    @Override
    public Mono<ResponseEntity<UserProfileResponse>> getProfile(ServerWebExchange exchange) {
        return currentPrincipal()
                .flatMap(getProfileUseCase::getProfile)
                .map(user -> ResponseEntity.ok(toResponse(user)));
    }

    @Override
    public Mono<ResponseEntity<UserProfileResponse>> updateProfile(Mono<UpdateProfileRequest> updateProfileRequest, ServerWebExchange exchange) {
        return currentPrincipal()
                .flatMap(principal -> updateProfileRequest.flatMap(request -> updateProfileUseCase.updateProfile(
                        principal,
                        request.getNickname(),
                        request.getAvatarUrl(),
                        request.getHeaderUrl(),
                        request.getPhoneNumber()
                )))
                .map(user -> ResponseEntity.ok(toResponse(user)));
    }

    private Mono<String> currentPrincipal() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName());
    }

    private UserProfileResponse toResponse(User user) {
        return new UserProfileResponse()
                .id(user.getId())
                .uuid(user.getUuid())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .headerUrl(user.getHeaderUrl())
                .phoneNumber(user.getPhoneNumber());
    }
}

package com.donation.app.infrastructure.web.api;

import com.donation.app.infrastructure.web.dto.UserProfileResponse;
import com.donation.app.infrastructure.web.dto.UpdateProfileRequest;
import reactor.core.publisher.Mono;

public interface ProfileApi {
    Mono<UserProfileResponse> getProfile(String email);
    Mono<UserProfileResponse> updateProfile(String currentEmail, UpdateProfileRequest request);
}

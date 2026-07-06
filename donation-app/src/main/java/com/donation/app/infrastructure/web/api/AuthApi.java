package com.donation.app.infrastructure.web.api;

import com.donation.app.infrastructure.web.dto.*;
import reactor.core.publisher.Mono;

public interface AuthApi {
    Mono<UserResponse> register(AuthRequest request);
    Mono<LoginResponse> login(AuthRequest request);
    Mono<LoginResponse> verifyMfaAndLogin(MfaVerificationRequest request);
}

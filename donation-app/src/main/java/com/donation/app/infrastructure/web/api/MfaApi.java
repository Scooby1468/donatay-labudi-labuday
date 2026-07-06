package com.donation.app.infrastructure.web.api;

import com.donation.app.infrastructure.web.dto.MfaSetupResponse;
import com.donation.app.infrastructure.web.dto.MfaVerificationRequest;
import reactor.core.publisher.Mono;

public interface MfaApi {
    Mono<MfaSetupResponse> setupGoogleMfa(String email);
    Mono<Void> setupSmsMfa(String email);
    Mono<String> sendSmsCode(String email);
    Mono<Boolean> verifyAndEnableMfa(String email, MfaVerificationRequest request);
}

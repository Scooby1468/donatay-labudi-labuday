package com.donation.app.infrastructure.web.api;

import com.donation.app.infrastructure.web.dto.AppVersionResponse;
import reactor.core.publisher.Mono;

public interface VersionApi {
    Mono<AppVersionResponse> getVersion();
}

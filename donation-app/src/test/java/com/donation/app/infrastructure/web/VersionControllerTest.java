package com.donation.app.infrastructure.web;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class VersionControllerTest {

    @Test
    void getVersion_ReturnsApplicationVersion() {
        StepVerifier.create(new VersionController("user-data-service", "0.1.0-test").getVersion(null))
                .expectNextMatches(response -> response.getStatusCode().is2xxSuccessful()
                        && "user-data-service".equals(response.getBody().getName())
                        && response.getBody().getVersion() != null
                        && response.getBody().getBuildTime() != null)
                .verifyComplete();
    }
}

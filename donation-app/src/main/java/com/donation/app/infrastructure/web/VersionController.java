package com.donation.app.infrastructure.web;

import com.donation.app.infrastructure.web.api.VersionApi;
import com.donation.app.infrastructure.web.dto.AppVersionResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;

@RestController
@Tag(name = "Версионирование", description = "Получение информации о текущей версии сервиса")
public class VersionController implements VersionApi {

    private final String applicationName;
    private final String applicationVersion;

    public VersionController(
            @Value("${spring.application.name:user-data-service}") String applicationName,
            @Value("${info.app.version:0.1.0-SNAPSHOT}") String applicationVersion) {
        this.applicationName = applicationName;
        this.applicationVersion = applicationVersion;
    }

    @Override
    public Mono<ResponseEntity<AppVersionResponse>> getVersion(ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(new AppVersionResponse()
                .name(applicationName)
                .version(applicationVersion)
                .commitHash("unknown")
                .buildTime(Instant.now().toString())));
    }
}

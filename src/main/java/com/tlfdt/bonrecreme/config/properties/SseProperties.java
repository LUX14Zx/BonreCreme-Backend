package com.tlfdt.bonrecreme.config.properties;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for Server-Sent Events (SSE) settings.
 */
@Component
@ConfigurationProperties(prefix = "application.sse")
@Data
@Validated
public class SseProperties {

    /**
     * The interval in seconds for sending keep-alive heartbeats to SSE clients.
     */
    @Positive
    private long heartbeatIntervalSeconds = 20;
}
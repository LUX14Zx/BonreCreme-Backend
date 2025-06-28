package com.tlfdt.bonrecreme.service.report.sse;

import com.tlfdt.bonrecreme.config.properties.SseProperties;
import com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.bill.BillResponseDTO;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Manages Server-Sent Event (SSE) connections for the manager's dashboard.
 * This service maintains a list of active manager connections and provides methods
 * to broadcast events, such as paid bill notifications. It includes a configurable
 * heartbeat to keep connections alive.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportSseService {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final SseProperties sseProperties; //

    /**
     * Creates a new SseEmitter for a manager client, adds it to the list of active emitters,
     * and configures lifecycle callbacks to ensure proper cleanup.
     *
     * @return A configured SseEmitter instance for the client.
     */
    public SseEmitter createEmitter() {
        // Use a very long timeout and rely on the heartbeat to keep the connection alive
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        this.emitters.add(emitter);
        log.info("New manager SSE client connected. Total clients: {}", emitters.size());

        // Register callbacks to remove the emitter from the list on completion, timeout, or error
        emitter.onCompletion(() -> removeEmitter(emitter, "completed"));
        emitter.onTimeout(() -> removeEmitter(emitter, "timed out"));
        emitter.onError(e -> removeEmitter(emitter, "errored: " + e.getMessage()));

        // Send an initial event to confirm the connection
        try {
            emitter.send(SseEmitter.event().name("connected").data("Manager reports stream connected."));
        } catch (IOException e) {
            log.error("Failed to send initial connection event for manager reports.", e);
            removeEmitter(emitter, "initial send failed");
        }

        return emitter;
    }

    /**
     * Sends a notification about a paid bill to all connected manager clients.
     *
     * @param bill The details of the bill that was paid.
     */
    public void sendPaidBillNotification(BillResponseDTO bill) {
        sendEventToAllEmitters(bill);
    }

    /**
     * Starts a periodic task to send keep-alive messages to all connected clients.
     * The interval is configured in application properties.
     */
    @PostConstruct
    private void startHeartbeat() {
        long interval = sseProperties.getHeartbeatIntervalSeconds(); //
        scheduler.scheduleAtFixedRate(this::broadcastHeartbeat, interval, interval, TimeUnit.SECONDS); //
        log.info("SSE heartbeat service started with a {} second interval.", interval);
    }

    /**
     * Shuts down the scheduler gracefully when the application is stopping.
     */
    @PreDestroy
    private void shutdownScheduler() {
        scheduler.shutdownNow();
        log.info("SSE heartbeat service shut down.");
    }

    /**
     * Iterates through all emitters and sends a comment, which acts as a keep-alive signal.
     */
    private void broadcastHeartbeat() {
        log.debug("Broadcasting SSE heartbeat to {} clients.", emitters.size());
        SseEventBuilder heartbeatEvent = SseEmitter.event().comment("keep-alive");

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(heartbeatEvent);
            } catch (IOException e) {
                // This is expected if a client disconnects without notice.
                // The onError callback will handle the removal of the emitter.
                log.warn("Failed to send heartbeat to a client. It will be removed on the next callback.");
            }
        }
    }

    /**
     * Sends a named event with a data payload to all connected SSE clients.
     *
     * @param data The payload for the event, which will be serialized to JSON.
     */
    private void sendEventToAllEmitters(Object data) {
        if (emitters.isEmpty()) {
            return;
        }

        log.info("Broadcasting event '{}' to {} manager clients.", "bill-paid", emitters.size());
        SseEventBuilder event = SseEmitter.event().name("bill-paid").data(data);

        for (SseEmitter emitter : this.emitters) {
            try {
                emitter.send(event);
            } catch (IOException e) {
                log.warn("Failed to send event to a manager client. It will be removed on the next callback. Error: {}", e.getMessage());
            }
        }
    }

    /**
     * Removes an emitter from the active list and logs the reason.
     * This method is thread-safe.
     *
     * @param emitter The emitter to remove.
     * @param reason  The reason for removal (e.g., "completed", "timed out").
     */
    private void removeEmitter(SseEmitter emitter, String reason) {
        if (this.emitters.remove(emitter)) {
            log.info("Manager SSE client disconnected (Reason: {}). Total clients remaining: {}", reason, emitters.size());
        }
    }
}
package com.tlfdt.bonrecreme.service.servation;

import com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto.OrderNotificationDTO;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
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
 * Manages Server-Sent Event (SSE) connections for waitstaff clients and listens
 * for relevant events from Kafka to broadcast. This service is designed for a
 * distributed environment, ensuring all instances notify their connected clients.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WaitstaffSseService {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    // Assuming you create and inject SseProperties as in previous examples
    // private final SseProperties sseProperties;

    /**
     * Creates a new SseEmitter for a waitstaff client and registers it for event broadcasting.
     * @return A configured SseEmitter instance.
     */
    public SseEmitter createEmitter() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        this.emitters.add(emitter);
        log.info("New Waitstaff SSE client connected. Total clients on this instance: {}", emitters.size());

        emitter.onCompletion(() -> removeEmitter(emitter, "completed"));
        emitter.onTimeout(() -> removeEmitter(emitter, "timed out"));
        emitter.onError(e -> removeEmitter(emitter, "errored: " + e.getMessage()));

        try {
            emitter.send(SseEmitter.event().name("connected").data("Connection established for waitstaff."));
        } catch (IOException e) {
            log.error("Failed to send initial connection event for waitstaff.", e);
            removeEmitter(emitter, "initial send failed");
        }
        return emitter;
    }

    /**
     * Listens for orders that are ready to be served from the "serve-order-topic".
     * This method leverages Spring's type-safe listener to automatically deserialize
     * the JSON message into an OrderNotificationDTO object.
     *
     * @param order The deserialized order notification from Kafka.
     */
    @KafkaListener(topics = "serve-order-topic", groupId = "waitstaff-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeReadyToServeOrder(OrderNotificationDTO order) {
        log.info("Consumed 'serve-order' event for order #{}, broadcasting to waitstaff.", order.getOrderId());
        sendOrderToWaitstaff(order);
    }

    /**
     * Sends a notification to all connected waitstaff that an order is ready to be served.
     *
     * @param order The details of the order that is ready.
     */
    private void sendOrderToWaitstaff(OrderNotificationDTO order) {
        if (emitters.isEmpty()) {
            return;
        }
        log.info("Broadcasting 'serve-order' event for order #{} to {} waitstaff clients on this instance.", order.getOrderId(), emitters.size());
        SseEventBuilder event = SseEmitter.event().name("serve-order").data(order);

        for (SseEmitter emitter : this.emitters) {
            try {
                emitter.send(event);
            } catch (IOException e) {
                log.warn("Failed to send event to waitstaff client, will be removed on next callback. Error: {}", e.getMessage());
            }
        }
    }

    @PostConstruct
    private void startHeartbeat() {
        // You can use SseProperties here for interval
        scheduler.scheduleAtFixedRate(this::broadcastHeartbeat, 20, 20, TimeUnit.SECONDS);
    }

    @PreDestroy
    private void shutdownScheduler() {
        scheduler.shutdownNow();
    }

    private void broadcastHeartbeat() {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().comment("keep-alive"));
            } catch (IOException e) {
                // Expected if client disconnects
            }
        }
    }

    private void removeEmitter(SseEmitter emitter, String reason) {
        if (this.emitters.remove(emitter)) {
            log.info("Waitstaff SSE client disconnected (Reason: {}). Total clients on this instance: {}", reason, emitters.size());
        }
    }
}
package com.tlfdt.bonrecreme.service.kitchen;

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
 * Manages Server-Sent Event (SSE) connections for the kitchen display.
 * <p>
 * This service is designed for a distributed environment. It listens to Kafka topics
 * for order events and broadcasts them to all clients connected to its specific
 * application instance. This ensures that events are propagated across all instances.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KitchenSseService {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * Creates a new SseEmitter for a client and registers it for event broadcasting.
     *
     * @return A configured {@link SseEmitter} instance for the client.
     */
    public SseEmitter createEmitter() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        this.emitters.add(emitter);
        log.info("New Kitchen SSE client connected. Total clients on this instance: {}", emitters.size());

        emitter.onCompletion(() -> removeEmitter(emitter, "completed"));
        emitter.onTimeout(() -> removeEmitter(emitter, "timed out"));
        emitter.onError(e -> removeEmitter(emitter, "errored: " + e.getMessage()));

        try {
            emitter.send(SseEmitter.event().name("connected").data("Connection established successfully."));
        } catch (IOException e) {
            log.error("Failed to send initial connection event.", e);
            removeEmitter(emitter, "initial send failed");
        }

        return emitter;
    }

    /**
     * Listens for new order events from the "order-topic" on Kafka.
     * The listener is configured to automatically deserialize the JSON message
     * into an OrderNotificationDTO object.
     *
     * @param order The deserialized order notification from Kafka.
     */
    @KafkaListener(topics = "order-topic", groupId = "kitchen-group")
    public void consumeNewOrder(OrderNotificationDTO order) {
        log.info("Consumed new order event from Kafka for Order ID {}", order.getOrderId());
        sendEventToAllEmitters("new-order", order);
    }

    /**
     * Listens for order update events from the "update-order-topic" on Kafka.
     *
     * @param order The deserialized order notification from Kafka.
     */
    @KafkaListener(topics = "update-order-topic", groupId = "kitchen-group")
    public void consumeOrderUpdate(OrderNotificationDTO order) {
        log.info("Consumed order update event from Kafka for Order ID {}", order.getOrderId());
        sendEventToAllEmitters("update-order", order);
    }

    private void sendEventToAllEmitters(String eventName, Object data) {
        if (emitters.isEmpty()) {
            return;
        }
        log.info("Broadcasting event '{}' to {} kitchen clients on this instance.", eventName, emitters.size());
        SseEventBuilder event = SseEmitter.event().name(eventName).data(data);

        for (SseEmitter emitter : this.emitters) {
            try {
                emitter.send(event);
            } catch (IOException e) {
                log.warn("Failed to send event to kitchen client, will be removed on next callback. Error: {}", e.getMessage());
            }
        }
    }

    // Unchanged methods from original file...

    @PostConstruct
    private void startHeartbeat() {
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
                // This is expected if a client disconnects abruptly.
            }
        }
    }

    private void removeEmitter(SseEmitter emitter, String reason) {
        if (this.emitters.remove(emitter)) {
            log.info("SSE client disconnected (Reason: {}). Total clients: {}", reason, emitters.size());
        }
    }
}
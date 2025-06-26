package com.tlfdt.bonrecreme.controller.api.v1.kitchen;

import com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto.OrderNotificationDTO;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/kitchen")
@Component // Mark as a component to be injectable
public class KitchenMonitorSse {

    private static final Logger LOGGER = LoggerFactory.getLogger(KitchenMonitorSse.class);
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    /**
     * Starts a background task to send a heartbeat every 15 seconds.
     * This keeps the SSE connection alive and prevents proxy timeouts.
     */
    @PostConstruct
    public void startHeartbeat() {
        scheduler.scheduleAtFixedRate(() -> {
            for (SseEmitter emitter : emitters) {
                try {
                    // SSE comments are used as heartbeats
                    emitter.send(SseEmitter.event().comment("keep-alive"));
                } catch (IOException e) {
                    // This can happen if the client disconnects, which is normal.
                    // The onError and onCompletion handlers will remove the emitter.
                    LOGGER.debug("Failed to send heartbeat, removing emitter.");
                    emitters.remove(emitter);
                }
            }
        }, 0, 20, TimeUnit.SECONDS);
    }

    @GetMapping("/stream")
    public SseEmitter streamOrders() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        // Send an initial event to confirm connection
        try {
            emitter.send(SseEmitter.event().name("connected").data("Connection established"));
        } catch (IOException e) {
            LOGGER.error("Failed to send initial connection event.", e);
        }

        this.emitters.add(emitter);

        // Set up handlers to remove the emitter when it's completed, times out, or has an error
        emitter.onCompletion(() -> {
            LOGGER.info("Emitter completed, removing from list.");
            this.emitters.remove(emitter);
        });
        emitter.onTimeout(() -> {
            LOGGER.info("Emitter timed out, removing from list.");
            this.emitters.remove(emitter);
        });
        emitter.onError(e -> {
            LOGGER.error("Emitter error: ", e);
            this.emitters.remove(emitter);
        });

        return emitter;
    }

    public void sendOrderToKitchen(OrderNotificationDTO order) {
        for (SseEmitter emitter : emitters) {
            try {
                SseEmitter.SseEventBuilder event = SseEmitter.event()
                        .data(order)
                        .name("new-order");
                emitter.send(event);
            } catch (IOException e) {
                LOGGER.error("Error sending order to emitter: {}", e.getMessage());
                emitters.remove(emitter);
            }
        }
    }

    public void sendOrderUpdateToKitchen(OrderNotificationDTO order) {
        for (SseEmitter emitter : emitters) {
            try {
                SseEmitter.SseEventBuilder event = SseEmitter.event()
                        .data(order)
                        .name("update-order"); // The new event name
                emitter.send(event);
            } catch (IOException e) {
                LOGGER.error("Error sending order update to emitter: {}", e.getMessage());
                emitters.remove(emitter);
            }
        }
    }

    /**
     * Gracefully shuts down the scheduler when the application is stopping.
     */
    @PreDestroy
    public void shutdownScheduler() {
        scheduler.shutdown();
    }
}
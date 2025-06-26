package com.tlfdt.bonrecreme.controller.api.v1.waitstaff;

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
@RequestMapping("/api/v1/waitstaff")
@Component
public class WaitstaffMonitorSse {

    private static final Logger LOGGER = LoggerFactory.getLogger(WaitstaffMonitorSse.class);
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void startHeartbeat() {
        scheduler.scheduleAtFixedRate(() -> {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event().comment("keep-alive"));
                } catch (IOException e) {
                    LOGGER.debug("Failed to send heartbeat, removing emitter.");
                    emitters.remove(emitter);
                }
            }
        }, 0, 20, TimeUnit.SECONDS);
    }

    @GetMapping("/stream")
    public SseEmitter streamReadyToServeOrders() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        try {
            emitter.send(SseEmitter.event().name("connected").data("Connection established for waitstaff"));
        } catch (IOException e) {
            LOGGER.error("Failed to send initial connection event for waitstaff.", e);
        }

        this.emitters.add(emitter);

        emitter.onCompletion(() -> {
            LOGGER.info("Emitter completed, removing from waitstaff list.");
            this.emitters.remove(emitter);
        });
        emitter.onTimeout(() -> {
            LOGGER.info("Emitter timed out, removing from waitstaff list.");
            this.emitters.remove(emitter);
        });
        emitter.onError(e -> {
            LOGGER.error("Emitter error in waitstaff: ", e);
            this.emitters.remove(emitter);
        });

        return emitter;
    }

    public void sendOrderToWaitstaff(OrderNotificationDTO order) {
        for (SseEmitter emitter : emitters) {
            try {
                SseEmitter.SseEventBuilder event = SseEmitter.event()
                        .data(order)
                        .name("serve-order");
                emitter.send(event);
            } catch (IOException e) {
                LOGGER.error("Error sending order to waitstaff emitter: {}", e.getMessage());
                emitters.remove(emitter);
            }
        }
    }

    @PreDestroy
    public void shutdownScheduler() {
        scheduler.shutdown();
    }
}
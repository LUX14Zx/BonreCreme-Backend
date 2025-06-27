package com.tlfdt.bonrecreme.controller.api.v1.manager.view;

import com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.bill.BillResponseDTO;
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
@RequestMapping("/api/v1/manager/reports")
@Component
public class ManagerReportMonitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagerReportMonitor.class);
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void startHeartbeat() {
        scheduler.scheduleAtFixedRate(() -> {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event().comment("keep-alive"));
                } catch (IOException e) {
                    LOGGER.debug("Failed to send heartbeat to manager, removing emitter.");
                    emitters.remove(emitter);
                }
            }
        }, 0, 25, TimeUnit.SECONDS);
    }

    @GetMapping("/stream")
    public SseEmitter streamReports() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        try {
            emitter.send(SseEmitter.event().name("connected").data("Manager report stream connected"));
        } catch (IOException e) {
            LOGGER.error("Failed to send initial connection event for manager reports.", e);
        }

        this.emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));

        return emitter;
    }

    public void sendPaidBillNotification(BillResponseDTO bill) {
        for (SseEmitter emitter : emitters) {
            try {
                SseEmitter.SseEventBuilder event = SseEmitter.event()
                        .data(bill)
                        .name("bill-paid");
                emitter.send(event);
            } catch (IOException e) {
                LOGGER.error("Error sending paid bill notification to manager: {}", e.getMessage());
                emitters.remove(emitter);
            }
        }
    }

    @PreDestroy
    public void shutdownScheduler() {
        scheduler.shutdown();
    }
}
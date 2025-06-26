package com.tlfdt.bonrecreme.controller.api.v1.kitchen;

import com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto.OrderNotificationDTO;
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

@RestController
@RequestMapping("/api/v1/kitchen")
@Component // Mark as a component to be injectable
public class KitchenSseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(KitchenSseController.class);
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @GetMapping("/stream")
    public SseEmitter streamOrders() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        this.emitters.add(emitter);

        emitter.onCompletion(() -> this.emitters.remove(emitter));
        emitter.onTimeout(() -> this.emitters.remove(emitter));
        emitter.onError(e -> this.emitters.remove(emitter));

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
}
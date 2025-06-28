package com.tlfdt.bonrecreme.controller.api.v1.kitchen.view;

import com.tlfdt.bonrecreme.service.kitchen.KitchenDisplayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * REST controller for the kitchen display.
 * Exposes an endpoint for clients (e.g., kitchen monitors) to subscribe to a
 * real-time stream of order events using Server-Sent Events (SSE).
 */
@RestController
@RequestMapping("/api/v1/kitchen")
@RequiredArgsConstructor
public class KitchenSseController {

    private final KitchenDisplayService kitchenDisplayService;

    /**
     * Establishes an SSE connection to stream order notifications.
     * Each client that calls this endpoint will receive future order events.
     *
     * @return An SseEmitter instance that manages the persistent connection.
     */
    @GetMapping("/stream")
    public SseEmitter streamOrders() {
        return kitchenDisplayService.createEmitter();
    }
}

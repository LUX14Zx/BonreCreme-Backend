package com.tlfdt.bonrecreme.controller.api.v1.waitstaff.view;

import com.tlfdt.bonrecreme.service.servation.WaitstaffSseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * REST controller for the waitstaff's real-time monitoring view.
 * Exposes an endpoint for waitstaff clients to subscribe to a stream of events,
 * such as notifications for orders that are ready to be served.
 */
@RestController
@RequestMapping("/api/v1/waitstaff")
@RequiredArgsConstructor
public class WaitstaffSseController {

    private final WaitstaffSseService waitstaffSseService;

    /**
     * Establishes an SSE connection to stream "ready-to-serve" order notifications.
     *
     * @return An SseEmitter instance that manages the persistent connection for a waitstaff client.
     */
    @GetMapping("/stream")
    public SseEmitter streamReadyToServeOrders() {
        return waitstaffSseService.createEmitter();
    }
}

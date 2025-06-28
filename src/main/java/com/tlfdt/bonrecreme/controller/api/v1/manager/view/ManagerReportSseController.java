package com.tlfdt.bonrecreme.controller.api.v1.manager.view;

import com.tlfdt.bonrecreme.service.report.sse.ReportSseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * REST controller for the manager's real-time reports monitoring.
 * Exposes an endpoint for manager dashboards to subscribe to a stream of live
 * events, such as paid bill notifications, using Server-Sent Events (SSE).
 */
@RestController
@RequestMapping("/api/v1/manager/reports")
@RequiredArgsConstructor
public class ManagerReportSseController {

    private final ReportSseService reportSseService;

    /**
     * Establishes an SSE connection to stream real-time reports-related events.
     *
     * @return An SseEmitter instance that manages the persistent connection for a client.
     */
    @GetMapping("/stream")
    public SseEmitter streamReportEvents() {
        return reportSseService.createEmitter();
    }
}

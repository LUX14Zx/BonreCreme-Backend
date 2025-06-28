package com.tlfdt.bonrecreme.controller.api.v1.waitstaff;

import com.tlfdt.bonrecreme.controller.api.v1.dto.ApiResponseDTO;
import com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto.OrderNotificationDTO;
import com.tlfdt.bonrecreme.service.order.OrderService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for waitstaff to manage the final serving status of an order.
 */
@RestController
@RequestMapping("/api/v1/waitstaff/order")
@RequiredArgsConstructor
@Validated // Enables validation for path variables.
public class ServeStatusController {

    private final OrderService orderService;

    /**
     * Marks an order as served, completing its lifecycle from the waitstaff's perspective.
     * This typically transitions the order to a 'SERVED' state.
     *
     * @param orderId The unique identifier of the order to be marked as served. Must be a positive number.
     * @return A standardized API response containing the details of the updated order.
     */
    @PutMapping("/serve/{orderId}")
    public ResponseEntity<ApiResponseDTO<OrderNotificationDTO>> serveOrder(
            @PathVariable @Positive(message = "Order ID must be a positive number.") Long orderId) {

        // Corrected method name to match the OrderService interface
        OrderNotificationDTO updatedOrder = orderService.markOrderAsServed(orderId);

        return ResponseEntity.ok(ApiResponseDTO.success(updatedOrder, "Order marked as SERVED."));
    }
}

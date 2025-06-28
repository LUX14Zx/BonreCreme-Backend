package com.tlfdt.bonrecreme.controller.api.v1.kitchen;

import com.tlfdt.bonrecreme.controller.api.v1.dto.ApiResponseDTO;
import com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto.OrderNotificationDTO;
import com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto.UpdateOrderStatusRequestDTO;
import com.tlfdt.bonrecreme.service.order.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for kitchen staff to manage the status of orders.
 * Provides endpoints to update an order's progress and mark it as ready for serving.
 */
@RestController
@RequestMapping("/api/v1/kitchen/order")
@RequiredArgsConstructor
@Validated // Enables validation for path variables and request parameters.
public class OrderStatusController {

    private final OrderService orderService;

    /**
     * Updates the status of a specific order (e.g., from PENDING to PREPARING).
     *
     * @param orderId    The unique identifier of the order to update. Must be a positive number.
     * @param requestDTO The request body containing the new status. Must be valid.
     * @return An ApiResponseDTO containing the details of the updated order.
     */
    @PutMapping("/update/{orderId}")
    public ResponseEntity<ApiResponseDTO<OrderNotificationDTO>> updateOrderStatus(
            @PathVariable @Positive(message = "Order ID must be a positive number.") Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequestDTO requestDTO) {

        OrderNotificationDTO updatedOrder = orderService.updateOrderStatus(orderId, requestDTO);

        ApiResponseDTO<OrderNotificationDTO> response = ApiResponseDTO.<OrderNotificationDTO>builder()
                .data(updatedOrder)
                .status("success")
                .message("Order status updated successfully.")
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Marks an order as ready to be served.
     * This is a specific status update, typically to the READY_TO_SERVE state.
     *
     * @param orderId The unique identifier of the order to be served. Must be a positive number.
     * @return An ApiResponseDTO containing the details of the updated order.
     */
    @PutMapping("/serve/{orderId}")
    public ResponseEntity<ApiResponseDTO<OrderNotificationDTO>> serveOrder(
            @PathVariable @Positive(message = "Order ID must be a positive number.") Long orderId) {

        // Corrected the method name to match the one in the OrderService interface
        OrderNotificationDTO updatedOrder = orderService.markOrderAsReadyToServe(orderId);

        ApiResponseDTO<OrderNotificationDTO> response = ApiResponseDTO.<OrderNotificationDTO>builder()
                .data(updatedOrder)
                .status("success")
                .message("Order marked as ready to serve.")
                .build();

        return ResponseEntity.ok(response);
    }
}

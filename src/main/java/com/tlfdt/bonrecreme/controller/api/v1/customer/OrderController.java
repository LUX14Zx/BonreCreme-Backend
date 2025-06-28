package com.tlfdt.bonrecreme.controller.api.v1.customer;

import com.tlfdt.bonrecreme.controller.api.v1.customer.dto.MenuRequestDTO;
import com.tlfdt.bonrecreme.controller.api.v1.dto.ApiResponseDTO;
import com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto.OrderNotificationDTO;
import com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto.UpdateOrderRequestDTO;
import com.tlfdt.bonrecreme.service.order.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling customer order operations, such as creating and updating orders.
 * All inputs are validated to ensure data integrity.
 */
@RestController
@RequestMapping("/api/v1/customer/order")
@RequiredArgsConstructor
@Validated
public class OrderController {

    private final OrderService orderService;

    /**
     * Creates a new order based on the provided menu request.
     *
     * @param menuRequestDTO The DTO containing the table ID and list of order items. Must be valid.
     * @return An ApiResponseDTO containing the notification details of the created order.
     */
    @PostMapping
    public ResponseEntity<ApiResponseDTO<OrderNotificationDTO>> createOrder(
            @Valid @RequestBody MenuRequestDTO menuRequestDTO) {
        OrderNotificationDTO createdOrder = orderService.createOrder(menuRequestDTO);

        ApiResponseDTO<OrderNotificationDTO> response = ApiResponseDTO.<OrderNotificationDTO>builder()
                .data(createdOrder)
                .status("success")
                .message("Order created successfully")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Updates an existing order with new or modified items.
     *
     * @param orderId    The unique identifier of the order to update. Must be a positive number.
     * @param requestDTO The DTO containing the items to add or update. Must be valid.
     * @return An ApiResponseDTO containing the notification details of the updated order.
     */
    @PutMapping("/update/{orderId}")
    public ResponseEntity<ApiResponseDTO<OrderNotificationDTO>> updateOrder(
            @PathVariable @Positive(message = "Order ID must be a positive number.") Long orderId,
            @Valid @RequestBody UpdateOrderRequestDTO requestDTO) {
        // Corrected method name to match the OrderService interface
        OrderNotificationDTO updatedOrder = orderService.updateOrderItems(orderId, requestDTO);

        ApiResponseDTO<OrderNotificationDTO> response = ApiResponseDTO.<OrderNotificationDTO>builder()
                .data(updatedOrder)
                .status("success")
                .message("Order updated successfully")
                .build();

        return ResponseEntity.ok(response);
    }
}

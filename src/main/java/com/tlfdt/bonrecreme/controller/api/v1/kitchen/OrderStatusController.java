package com.tlfdt.bonrecreme.controller.api.v1.kitchen;

import com.tlfdt.bonrecreme.controller.api.v1.dto.ApiResponseDTO;
import com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto.OrderNotificationDTO;
import com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto.UpdateOrderStatusRequestDTO;
import com.tlfdt.bonrecreme.service.order.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/kitchen/order")
@RequiredArgsConstructor
public class OrderStatusController {

    private final OrderService orderService;

    @PutMapping(path = "/update/{orderId}")
    public ResponseEntity<ApiResponseDTO<OrderNotificationDTO>> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequestDTO requestDTO) {

        OrderNotificationDTO updatedOrder = orderService.updateOrderStatus(orderId, requestDTO);

        ApiResponseDTO<OrderNotificationDTO> response = ApiResponseDTO.<OrderNotificationDTO>builder()
                .api_data(updatedOrder)
                .status("success")
                .message("Order updated successfully")
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(path = "/update/{orderId}/ready")
    public ResponseEntity<ApiResponseDTO<OrderNotificationDTO>> orderReadyToServe(
            @PathVariable Long orderId) {

        OrderNotificationDTO updatedOrder = orderService.updateOrderStatusReadyToServe(orderId);

        ApiResponseDTO<OrderNotificationDTO> response = ApiResponseDTO.<OrderNotificationDTO>builder()
                .api_data(updatedOrder)
                .status("success")
                .message("Order updated successfully")
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
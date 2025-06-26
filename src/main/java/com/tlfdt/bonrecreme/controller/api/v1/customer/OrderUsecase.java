package com.tlfdt.bonrecreme.controller.api.v1.customer;

import com.tlfdt.bonrecreme.controller.api.v1.customer.dto.MenuRequestDTO;
import com.tlfdt.bonrecreme.controller.api.v1.dto.ApiResponseDTO;
import com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto.OrderNotificationDTO;
import com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto.UpdateOrderRequestDTO;
import com.tlfdt.bonrecreme.service.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customer/order")
public class OrderUsecase {

    private final OrderService orderService;

    @Autowired
    public OrderUsecase(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<OrderNotificationDTO>> createOrder(@RequestBody MenuRequestDTO menuRequestDTO) {
        OrderNotificationDTO createdOrder = orderService.createOrder(menuRequestDTO);

        ApiResponseDTO<OrderNotificationDTO> response = ApiResponseDTO.<OrderNotificationDTO>builder()
                .api_data(createdOrder)
                .status("success")
                .message("Order created successfully")
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/update/{orderId}")
    public ResponseEntity<ApiResponseDTO<OrderNotificationDTO>> updateOrder(
            @PathVariable Long orderId,
            @RequestBody UpdateOrderRequestDTO requestDTO) {

        OrderNotificationDTO updatedOrder = orderService.updateOrder(orderId, requestDTO);

        ApiResponseDTO<OrderNotificationDTO> response = ApiResponseDTO.<OrderNotificationDTO>builder()
                .api_data(updatedOrder)
                .status("success")
                .message("Order updated successfully")
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
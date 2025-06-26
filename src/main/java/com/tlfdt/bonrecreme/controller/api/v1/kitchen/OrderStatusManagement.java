package com.tlfdt.bonrecreme.controller.api.v1.kitchen;

import com.tlfdt.bonrecreme.controller.api.v1.dto.ApiResponseDTO;
import com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto.UpdateOrderRequestDTO;
import com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto.UpdateOrderStatusRequestDTO;
import com.tlfdt.bonrecreme.model.restaurant.Order;
import com.tlfdt.bonrecreme.service.order.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/v1/kitchen/order")
@RequiredArgsConstructor
public class OrderStatusManagement {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderStatusManagement.class);
    private final OrderService orderService;

    @PutMapping(
            path = "/update/{orderId}"
    )
    public ResponseEntity<ApiResponseDTO<Order>> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequestDTO requestDTO) {

        Order updatedOrder = orderService.updateOrderStatus(orderId, requestDTO);

        ApiResponseDTO<Order> response = ApiResponseDTO.<Order>builder()
                .api_data(updatedOrder)
                .status("success")
                .message("Order updated successfully")
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

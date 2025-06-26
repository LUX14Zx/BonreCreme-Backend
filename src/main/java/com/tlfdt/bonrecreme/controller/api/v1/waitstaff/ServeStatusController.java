package com.tlfdt.bonrecreme.controller.api.v1.waitstaff;

import com.tlfdt.bonrecreme.controller.api.v1.dto.ApiResponseDTO;
import com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto.UpdateOrderStatusRequestDTO;
import com.tlfdt.bonrecreme.model.restaurant.Order;
import com.tlfdt.bonrecreme.service.order.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/waitstaff/order")
@RequiredArgsConstructor
public class ServeStatusController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServeStatusController.class);
    private final OrderService orderService;

    @PutMapping(
            path = "/serve/{orderId}"
    )
    public ResponseEntity<ApiResponseDTO<Order>> serveOrder(
            @PathVariable Long orderId) {

        Order updatedOrder = orderService.serveOrder(orderId);

        ApiResponseDTO<Order> response = ApiResponseDTO.<Order>builder()
                .api_data(updatedOrder)
                .status("success")
                .message("Order updated successfully")
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

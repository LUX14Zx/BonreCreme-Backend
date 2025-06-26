package com.tlfdt.bonrecreme.controller.api.v1.waitstaff;

import com.tlfdt.bonrecreme.controller.api.v1.dto.ApiResponseDTO;
import com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto.OrderNotificationDTO;
import com.tlfdt.bonrecreme.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/waitstaff/order")
@RequiredArgsConstructor
public class ServeStatusController {

    private final OrderService orderService;

    @PutMapping(path = "/serve/{orderId}")
    public ResponseEntity<ApiResponseDTO<OrderNotificationDTO>> serveOrder(
            @PathVariable Long orderId) {

        OrderNotificationDTO updatedOrder = orderService.serveOrder(orderId);

        ApiResponseDTO<OrderNotificationDTO> response = ApiResponseDTO.<OrderNotificationDTO>builder()
                .api_data(updatedOrder)
                .status("success")
                .message("Order updated successfully")
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
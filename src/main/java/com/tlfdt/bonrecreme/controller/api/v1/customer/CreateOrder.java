package com.tlfdt.bonrecreme.controller.api.v1.customer;

import com.tlfdt.bonrecreme.service.order.OrderServiceImpl;
import com.tlfdt.bonrecreme.controller.api.v1.customer.dto.MenuRequestDTO;
import com.tlfdt.bonrecreme.controller.api.v1.dto.ApiResponseDTO;
import com.tlfdt.bonrecreme.model.restaurant.Order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customer/menu")
public class CreateOrder {

    private final OrderServiceImpl orderService;

    @Autowired
    public CreateOrder(OrderServiceImpl orderService) {
        this.orderService = orderService;
    }
    /**
     * Creates a new order with a list of order items.
     * Delegates the core logic to the OrderService.
     *
     * @param menuRequestDTO The order request containing table ID and order items.
     * @return A ResponseEntity containing the created order.
     */
    @PostMapping("/order")
    public ResponseEntity<ApiResponseDTO<Order>> createOrder(@RequestBody MenuRequestDTO menuRequestDTO) {

        // Delegate the business logic to the service layer
        Order createdOrder = orderService.createOrder(menuRequestDTO);

        // Build a standardized API response
        ApiResponseDTO<Order> response = ApiResponseDTO.<Order>builder()
                .api_data(createdOrder)
                .status("success")
                .message("Order created successfully")
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}


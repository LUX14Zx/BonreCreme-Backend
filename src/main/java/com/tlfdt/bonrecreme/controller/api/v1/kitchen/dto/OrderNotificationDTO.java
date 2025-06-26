package com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto;

import com.tlfdt.bonrecreme.model.restaurant.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderNotificationDTO implements Serializable {
    private Long orderId;
    private Integer tableNumber;
    private List<OrderItemDTO> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDTO implements Serializable {
        private String menuItemName;
        private Integer quantity;
        private String specialRequests;
    }

    public static OrderNotificationDTO fromOrder(Order order) {
        List<OrderItemDTO> itemDTOs = order.getOrderItems().stream()
                .map(orderItem -> new OrderItemDTO(
                        orderItem.getMenuItem().getName(),
                        orderItem.getQuantity(),
                        orderItem.getSpecialRequests()
                ))
                .collect(Collectors.toList());

        return new OrderNotificationDTO(
                order.getId(),
                order.getSeatTable().getTableNumber(),
                itemDTOs
        );
    }
}
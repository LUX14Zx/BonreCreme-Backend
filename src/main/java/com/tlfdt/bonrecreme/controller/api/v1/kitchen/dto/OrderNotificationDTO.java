package com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tlfdt.bonrecreme.model.restaurant.Order;
import com.tlfdt.bonrecreme.model.restaurant.OrderItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents an immutable, validated data transfer object for notifying other systems
 * (e.g., the kitchen) about a new or updated order. This DTO is designed to be
 * sent over a message queue like Kafka.
 */
@Value
public class OrderNotificationDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The unique identifier of the order. Must be a positive number.
     */
    @NotNull(message = "Order ID cannot be null.")
    @Positive(message = "Order ID must be a positive number.")
    @JsonProperty("order_id")
    Long orderId;

    /**
     * The ID of the table where the order was placed. Must be a positive number.
     */
    @NotNull(message = "Table ID cannot be null.")
    @Positive(message = "Table ID must be a positive number.")
    @JsonProperty("table_id")
    Long tableId;

    /**
     * A list of items included in the order. Must not be empty.
     */
    @NotEmpty(message = "Order must contain at least one item.")
    @Valid
    List<OrderItemDTO> items;

    @JsonCreator
    public OrderNotificationDTO(
            @JsonProperty("order_id") Long orderId,
            @JsonProperty("table_id") Long tableId,
            @JsonProperty("items") List<OrderItemDTO> items) {
        this.orderId = orderId;
        this.tableId = tableId;
        this.items = items;
    }

    /**
     * Represents a single, immutable item within an order notification.
     */
    @Value
    public static class OrderItemDTO implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * The name of the menu item. Cannot be blank.
         */
        @NotBlank(message = "Menu item name cannot be blank.")
        @JsonProperty("menu_item_name")
        String menuItemName;

        /**
         * The quantity of the menu item ordered. Must be a positive number.
         */
        @NotNull(message = "Quantity cannot be null.")
        @Positive(message = "Quantity must be a positive number.")
        Integer quantity;

        /**
         * Any special requests for this specific item. Limited to 255 characters.
         */
        @Size(max = 255, message = "Special requests cannot exceed 255 characters.")
        @JsonProperty("special_requests")
        String specialRequests;

        @JsonCreator
        public OrderItemDTO(
                @JsonProperty("menu_item_name") String menuItemName,
                @JsonProperty("quantity") Integer quantity,
                @JsonProperty("special_requests") String specialRequests) {
            this.menuItemName = menuItemName;
            this.quantity = quantity;
            this.specialRequests = specialRequests;
        }

        /**
         * Constructs an OrderItemDTO from an OrderItem domain entity.
         *
         * @param orderItem The OrderItem entity to convert.
         */
        public OrderItemDTO(OrderItem orderItem) {
            this.menuItemName = orderItem.getMenuItem().getName();
            this.quantity = orderItem.getQuantity();
            this.specialRequests = orderItem.getSpecialRequests();
        }
    }

    /**
     * A static factory method to create an OrderNotificationDTO from an Order entity.
     *
     * @param order The source Order entity.
     * @return A new, populated OrderNotificationDTO.
     */
    public static OrderNotificationDTO fromOrder(Order order) {
        if (order == null || order.getSeatTable() == null) {
            throw new IllegalArgumentException("Order and its associated table must not be null.");
        }

        List<OrderItemDTO> itemDTOs = order.getOrderItems().stream()
                .map(OrderItemDTO::new)
                .collect(Collectors.toList());

        return new OrderNotificationDTO(
                order.getId(),
                order.getSeatTable().getId(), // Use the table ID instead of number
                itemDTOs
        );
    }
}
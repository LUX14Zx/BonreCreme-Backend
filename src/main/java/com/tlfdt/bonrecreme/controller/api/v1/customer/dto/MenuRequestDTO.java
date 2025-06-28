package com.tlfdt.bonrecreme.controller.api.v1.customer.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.Range;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Represents the data transfer object for creating or updating a customer's order.
 * This class is designed to be immutable to ensure data integrity after creation.
 * It includes robust validation to secure the application at the entry point.
 */

@Value // Combines @Getter, @AllArgsConstructor, @ToString, @EqualsAndHashCode
@Builder
public class MenuRequestDTO  {
    /**
     * The unique identifier of the table where the order is placed.
     * Must be a positive number.
     */
    @NotNull(message = "Table ID cannot be null.")
    @Positive(message = "Table ID must be a positive number.")
    Long tableId;

    /**
     * A list of items included in the order.
     * The list cannot be empty and is limited to a maximum of 50 items to prevent abuse.
     * Validation is cascaded to the OrderItemRequest objects within this list.
     */
    @NotEmpty(message = "Order must contain at least one item.")
    @Size(max = 50, message = "An order cannot contain more than 50 different items.")
    @Valid // This is crucial to trigger validation on the objects within the list
            List<OrderItemRequest> items;

    @JsonCreator
    public MenuRequestDTO(
            @JsonProperty("tableId") Long tableId,
            @JsonProperty("items") List<OrderItemRequest> items) {
        this.tableId = tableId;
        this.items = items;
    }

    /**
     * Represents a single item within an order request.
     * This class is also immutable and includes field-level validation.
     */
    @Value // Combines @Getter, @AllArgsConstructor, @ToString, @EqualsAndHashCode
    @Builder
    public static class OrderItemRequest {

        /**
         * The unique identifier for the menu item being ordered.
         * Must be a positive number.
         */
        @NotNull(message = "Menu Item ID cannot be null.")
        @Positive(message = "Menu Item ID must be a positive number.")
        Long menuItemId;

        /**
         * The quantity of the menu item being ordered.
         * Must be between 1 and 100.
         */
        @NotNull(message = "Quantity cannot be null.")
        @Range(min = 1, max = 1000, message = "Quantity must be between 1 and 1000.")
        Integer quantity;

        /**
         * Any special requests for this order item.
         * To prevent Cross-Site Scripting (XSS), this field only allows a limited set of characters
         * and disallows HTML tags or other potentially malicious code.
         * The length is limited to 255 characters.
         */
        @Pattern(regexp = "^[a-zA-Z0-9 .,'\"-]*$", message = "Special requests contain invalid characters.")
        @Size(max = 255, message = "Special requests cannot exceed 255 characters.")
        String specialRequests;

        @JsonCreator
        public OrderItemRequest(
                @JsonProperty("menuItemId") Long menuItemId,
                @JsonProperty("quantity") Integer quantity,
                @JsonProperty("specialRequests") String specialRequests) {
            this.menuItemId = menuItemId;
            this.quantity = quantity;
            this.specialRequests = specialRequests;
        }
    }
}
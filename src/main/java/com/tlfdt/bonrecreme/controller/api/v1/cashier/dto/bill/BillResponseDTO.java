package com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.bill;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents the immutable data transfer object for a detailed bill response.
 * This DTO is designed with a builder for ease of construction, while validation ensures
 * the integrity and security of the data sent to the client.
 */
@Value // Combines @Getter, @AllArgsConstructor, @ToString, @EqualsAndHashCode
@Builder
public class BillResponseDTO {

    /**
     * The unique identifier for the bill. Must be a positive number.
     */
    @NotNull(message = "Bill ID cannot be null.")
    @Positive(message = "Bill ID must be a positive number.")
    @JsonProperty("bill_id")
    Long billId;

    /**
     * The number of the table associated with the bill. Must be a positive number.
     */
    @NotNull(message = "table number cannot be null.")
    @Positive(message = "table number must be a positive number.")
    @JsonProperty("table_number")
    Long tableNumber;

    /**
     * The total monetary amount of the entire bill. Must be a non-negative value.
     */
    @NotNull(message = "Total amount cannot be null.")
    @DecimalMin(value = "0.0", message = "Total amount must not be negative.")
    @JsonProperty("total_amount")
    BigDecimal totalAmount;

    /**
     * The date and time when the bill was finalized. Must not be null.
     * We add @JsonFormat to ensure it's serialized as a standard ISO-8601 string.
     */
    @NotNull(message = "Bill time cannot be null.")
    @JsonProperty("bill_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    LocalDateTime billTime;

    /**
     * The payment status of the bill. Must not be null.
     */
    @NotNull(message = "isPaid status cannot be null.")
    @JsonProperty("is_paid")
    boolean isPaid;

    /**
     * A list of orders associated with the bill. The list cannot be empty.
     * Validation is cascaded to the OrderDTO objects within this list.
     */
    @NotEmpty(message = "A bill must contain at least one order.")
    @Valid
    @JsonProperty("orders")
    List<OrderDTO> orders;

    /**
     * Represents a single, immutable order within a bill.
     */
    @Value
    @Builder
    public static class OrderDTO {
        /**
         * The unique identifier for the order. Must be a positive number.
         */
        @NotNull(message = "Order ID cannot be null.")
        @Positive(message = "Order ID must be a positive number.")
        @JsonProperty("order_id")
        Long orderId;

        /**
         * The total price for this specific order. Must be a non-negative value.
         */
        @NotNull(message = "Total price cannot be null.")
        @DecimalMin(value = "0.0", message = "Total price must not be negative.")
        @JsonProperty("total_price")
        BigDecimal totalPrice;

        /**
         * A list of items included in the order. The list cannot be empty.
         * Validation is cascaded to the OrderItemDTO objects.
         */
        @NotEmpty(message = "An order must contain at least one item.")
        @Valid
        @JsonProperty("items")
        List<OrderItemDTO> items;
    }

    /**
     * Represents a single, immutable item within an order.
     */
    @Value
    @Builder
    public static class OrderItemDTO {
        /**
         * The name of the menu item. Cannot be blank.
         */
        @NotBlank(message = "Item name cannot be blank.")
        @JsonProperty("name")
        String name;

        /**
         * The quantity of this menu item. Must be a positive integer.
         */
        @NotNull(message = "Quantity cannot be null.")
        @Positive(message = "Quantity must be a positive number.")
        @JsonProperty("quantity")
        int quantity;

        /**
         * The price of a single unit of this item. Must be a non-negative value.
         */
        @NotNull(message = "Price cannot be null.")
        @DecimalMin(value = "0.0", message = "Price must not be negative.")
        @JsonProperty("price")
        BigDecimal price;
    }
}

package com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.bill;

import com.fasterxml.jackson.annotation.JsonCreator;
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

    @NotNull(message = "Bill ID cannot be null.")
    @Positive(message = "Bill ID must be a positive number.")
    @JsonProperty("bill_id")
    Long billId;

    @NotNull(message = "Table ID cannot be null.")
    @Positive(message = "Table ID must be a positive number.")
    @JsonProperty("table_id")
    Long tableId;

    @NotNull(message = "Total amount cannot be null.")
    @DecimalMin(value = "0.0", message = "Total amount must not be negative.")
    @JsonProperty("total_amount")
    BigDecimal totalAmount;

    @NotNull(message = "Bill time cannot be null.")
    @JsonProperty("bill_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    LocalDateTime billTime;

    @NotNull(message = "isPaid status cannot be null.")
    @JsonProperty("is_paid")
    boolean isPaid;

    @NotEmpty(message = "A bill must contain at least one order.")
    @Valid
    @JsonProperty("orders")
    List<OrderDTO> orders;

    // Added @JsonCreator constructor for deserialization
    @JsonCreator
    public BillResponseDTO(
            @JsonProperty("bill_id") Long billId,
            @JsonProperty("table_id") Long tableId,
            @JsonProperty("total_amount") BigDecimal totalAmount,
            @JsonProperty("bill_time") LocalDateTime billTime,
            @JsonProperty("is_paid") boolean isPaid,
            @JsonProperty("orders") List<OrderDTO> orders) {
        this.billId = billId;
        this.tableId = tableId;
        this.totalAmount = totalAmount;
        this.billTime = billTime;
        this.isPaid = isPaid;
        this.orders = orders;
    }

    @Value
    @Builder
    public static class OrderDTO {
        @NotNull(message = "Order ID cannot be null.")
        @Positive(message = "Order ID must be a positive number.")
        @JsonProperty("order_id")
        Long orderId;

        @NotNull(message = "Total price cannot be null.")
        @DecimalMin(value = "0.0", message = "Total price must not be negative.")
        @JsonProperty("total_price")
        BigDecimal totalPrice;

        @NotEmpty(message = "An order must contain at least one item.")
        @Valid
        @JsonProperty("items")
        List<OrderItemDTO> items;

        // Added @JsonCreator constructor for deserialization
        @JsonCreator
        public OrderDTO(
                @JsonProperty("order_id") Long orderId,
                @JsonProperty("total_price") BigDecimal totalPrice,
                @JsonProperty("items") List<OrderItemDTO> items) {
            this.orderId = orderId;
            this.totalPrice = totalPrice;
            this.items = items;
        }
    }

    @Value
    @Builder
    public static class OrderItemDTO {
        @NotBlank(message = "Item name cannot be blank.")
        @JsonProperty("name")
        String name;

        @NotNull(message = "Quantity cannot be null.")
        @Positive(message = "Quantity must be a positive number.")
        @JsonProperty("quantity")
        int quantity;

        @NotNull(message = "Price cannot be null.")
        @DecimalMin(value = "0.0", message = "Price must not be negative.")
        @JsonProperty("price")
        BigDecimal price;

        // Added @JsonCreator constructor for deserialization
        @JsonCreator
        public OrderItemDTO(
                @JsonProperty("name") String name,
                @JsonProperty("quantity") int quantity,
                @JsonProperty("price") BigDecimal price) {
            this.name = name;
            this.quantity = quantity;
            this.price = price;
        }
    }
}
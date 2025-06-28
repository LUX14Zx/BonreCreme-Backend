package com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tlfdt.bonrecreme.model.restaurant.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents an immutable and type-safe data transfer object for updating the
 * status of an existing order. This DTO is used as a request body.
 */
@Value
public class UpdateOrderStatusRequestDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The new status for the order.
     * Must be one of the valid values defined in the OrderStatus enum (e.g., "PREPARING", "READY").
     * The framework will automatically reject requests with invalid status values.
     */
    @NotNull(message = "Order status cannot be null.")
    @JsonProperty("status")
    OrderStatus status;

    @JsonCreator
    public UpdateOrderStatusRequestDTO(@JsonProperty("status") OrderStatus status) {
        this.status = status;
    }
}
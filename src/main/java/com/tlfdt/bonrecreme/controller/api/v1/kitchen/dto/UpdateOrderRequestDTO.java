package com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tlfdt.bonrecreme.controller.api.v1.customer.dto.MenuRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Value;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Represents an immutable and validated data transfer object for updating an existing order.
 * It contains a list of items to be added or modified in the order.
 */
@Value
public class UpdateOrderRequestDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * A list of items to be added to or updated in the order.
     * The list must not be null or empty, and each item within the list will be validated.
     */
    @JsonProperty("items")
    @NotEmpty(message = "The items list cannot be empty.")
    @Valid // This is crucial to trigger validation on the OrderItemRequest objects.
            List<MenuRequestDTO.OrderItemRequest> items;

    @JsonCreator
    public UpdateOrderRequestDTO(@JsonProperty("items") List<MenuRequestDTO.OrderItemRequest> items) {
        this.items = items;
    }
}
package com.tlfdt.bonrecreme.controller.api.v1.manager.dto.table;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tlfdt.bonrecreme.model.restaurant.enums.TableStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Value;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents an immutable and validated data transfer object for creating or updating a restaurant table.
 * This DTO is used as a request body in manager-related table management endpoints.
 */
@Value
public class TableRequestDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The maximum number of guests the table can accommodate. Must be a positive integer.
     */
    @NotNull(message = "Seating capacity cannot be null.")
    @Positive(message = "Seating capacity must be a positive integer.")
    @JsonProperty("seating_capacity")
    Integer seatingCapacity;

    /**
     * The status of the table (e.g., AVAILABLE, OCCUPIED).
     * This field is optional; if not provided, the status will not be updated.
     * Must be one of the values from the TableStatus enum.
     */
    @JsonProperty("status")
    TableStatus status;

    @JsonCreator
    public TableRequestDTO(
            @JsonProperty("seating_capacity") Integer seatingCapacity,
            @JsonProperty("status") TableStatus status) {
        this.seatingCapacity = seatingCapacity;
        this.status = status;
    }
}
package com.tlfdt.bonrecreme.controller.api.v1.manager.dto.table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tlfdt.bonrecreme.model.restaurant.SeatTable;
import com.tlfdt.bonrecreme.model.restaurant.enums.TableStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Value;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents an immutable and validated data transfer object for a restaurant table response.
 * This DTO is used to send details of a table to clients.
 */
@Value
public class TableResponseDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The unique identifier of the table. Must be a positive number.
     */
    @NotNull(message = "ID cannot be null.")
    @Positive(message = "ID must be a positive number.")
    @JsonProperty("id")
    Long id;

    /**
     * The unique number assigned to the table. Must be a positive integer.
     */
    @NotNull(message = "table number cannot be null.")
    @Positive(message = "table number must be a positive integer.")
    @JsonProperty("table_number")
    Integer tableNumber;

    /**
     * The maximum number of guests the table can accommodate. Must be a positive integer.
     */
    @NotNull(message = "Seating capacity cannot be null.")
    @Positive(message = "Seating capacity must be a positive integer.")
    @JsonProperty("seating_capacity")
    Integer seatingCapacity;

    /**
     * The current status of the table (e.g., AVAILABLE, OCCUPIED). Must not be null.
     */
    @NotNull(message = "Status cannot be null.")
    @JsonProperty("status")
    TableStatus status;

    /**
     * Constructs a TableResponseDTO from a SeatTable domain entity.
     * This encapsulates the mapping logic directly within the DTO.
     *
     * @param table The SeatTable entity to convert.
     */
    public TableResponseDTO(SeatTable table) {
        this.id = table.getId();
        this.tableNumber = table.getTableNumber();
        this.seatingCapacity = table.getSeatingCapacity();
        this.status = table.getStatus();
    }

    /**
     * A static factory method to create a TableResponseDTO from a SeatTable entity.
     * This provides a clear and convenient way to perform the conversion.
     *
     * @param seatTable The source SeatTable entity.
     * @return A new, populated TableResponseDTO.
     */
    public static TableResponseDTO fromSeatTable(SeatTable seatTable) {
        if (seatTable == null) {
            return null;
        }
        return new TableResponseDTO(seatTable);
    }
}

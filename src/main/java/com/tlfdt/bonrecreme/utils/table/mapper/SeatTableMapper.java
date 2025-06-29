package com.tlfdt.bonrecreme.utils.table.mapper;

import com.tlfdt.bonrecreme.controller.api.v1.manager.dto.table.TableRequestDTO;
import com.tlfdt.bonrecreme.controller.api.v1.manager.dto.table.TableResponseDTO;
import com.tlfdt.bonrecreme.model.restaurant.SeatTable;
import com.tlfdt.bonrecreme.model.restaurant.enums.TableStatus;
import org.springframework.stereotype.Component;

/**
 * A component responsible for mapping between SeatTable entities and DTOs.
 * This promotes a clean separation of concerns between the data layer and the API layer.
 */
@Component
public class SeatTableMapper {

    /**
     * Converts a SeatTable entity to a TableResponseDTO.
     *
     * @param seatTable The entity to convert.
     * @return The corresponding DTO.
     */
    public TableResponseDTO toResponseDTO(SeatTable seatTable) {
        if (seatTable == null) {
            return null;
        }
        return TableResponseDTO.fromSeatTable(seatTable);
    }

    /**
     * Creates a new SeatTable entity from a TableRequestDTO.
     * Sets the default status for a new table to AVAILABLE.
     *
     * @param requestDTO The DTO containing the data for the new table.
     * @return A new, unsaved SeatTable entity.
     */
    public SeatTable toNewEntity(TableRequestDTO requestDTO) {
        return SeatTable.builder()
                .seatingCapacity(requestDTO.getSeatingCapacity())
                .status(TableStatus.AVAILABLE) // New tables are always available by default
                .build();
    }

    /**
     * Updates an existing SeatTable entity with data from a TableRequestDTO.
     *
     * @param seatTable  The entity to update.
     * @param requestDTO The DTO containing the new data.
     */
    public void updateEntityFromDTO(SeatTable seatTable, TableRequestDTO requestDTO) {
        seatTable.setSeatingCapacity(requestDTO.getSeatingCapacity());
        seatTable.setStatus(requestDTO.getStatus());
    }
}

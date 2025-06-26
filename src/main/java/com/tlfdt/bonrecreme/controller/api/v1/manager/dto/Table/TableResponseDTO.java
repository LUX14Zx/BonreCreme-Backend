package com.tlfdt.bonrecreme.controller.api.v1.manager.dto.Table;

import com.tlfdt.bonrecreme.model.restaurant.SeatTable;
import com.tlfdt.bonrecreme.model.restaurant.enums.TableStatus;
import lombok.Data;

@Data
public class TableResponseDTO {
    private Long id;
    private Integer tableNumber;
    private Integer seatingCapacity;
    private TableStatus status;

    public static TableResponseDTO fromRestaurantTable(SeatTable table) {
        TableResponseDTO dto = new TableResponseDTO();
        dto.setId(table.getId());
        dto.setTableNumber(table.getTableNumber());
        dto.setSeatingCapacity(table.getSeatingCapacity());
        dto.setStatus(table.getStatus());
        return dto;
    }
}
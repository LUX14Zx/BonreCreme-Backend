package com.tlfdt.bonrecreme.controller.api.v1.manager.dto.Table;

import com.tlfdt.bonrecreme.model.restaurant.enums.TableStatus;
import lombok.Data;

@Data
public class TableRequestDTO {
    private Integer tableNumber;
    private Integer seatingCapacity;
    private TableStatus status;
}

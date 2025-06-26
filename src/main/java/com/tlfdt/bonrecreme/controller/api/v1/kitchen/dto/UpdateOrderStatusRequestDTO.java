package com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto;

import com.tlfdt.bonrecreme.controller.api.v1.customer.dto.MenuRequestDTO;
import com.tlfdt.bonrecreme.model.restaurant.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class UpdateOrderStatusRequestDTO {
    @NotNull
    private String status;
}
package com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto;

import com.tlfdt.bonrecreme.controller.api.v1.customer.dto.MenuRequestDTO;
import lombok.Data;
import java.util.List;

@Data
public class UpdateOrderRequestDTO {
    private List<MenuRequestDTO.OrderItemRequest> items;
}
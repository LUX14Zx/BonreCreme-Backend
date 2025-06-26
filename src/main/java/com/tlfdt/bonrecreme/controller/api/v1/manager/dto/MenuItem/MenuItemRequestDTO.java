package com.tlfdt.bonrecreme.controller.api.v1.manager.dto.MenuItem;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class MenuItemRequestDTO {
    private String name;
    private String description;
    private BigDecimal price;
}
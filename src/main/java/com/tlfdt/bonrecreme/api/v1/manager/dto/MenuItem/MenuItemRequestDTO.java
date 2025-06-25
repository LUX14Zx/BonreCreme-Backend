package com.tlfdt.bonrecreme.api.v1.manager.dto.MenuItem;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class MenuItemRequestDTO {
    private String name;
    private String description;
    private BigDecimal price;
}
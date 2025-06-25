package com.tlfdt.bonrecreme.api.v1.manager.dto;

import com.tlfdt.bonrecreme.model.restaurant.MenuItem;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class MenuItemResponseDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;

    public static MenuItemResponseDTO fromMenuItem(MenuItem menuItem) {
        MenuItemResponseDTO menuItemResponse = new MenuItemResponseDTO();
        menuItemResponse.setId(menuItem.getId());
        menuItemResponse.setName(menuItem.getName());
        menuItemResponse.setDescription(menuItem.getDescription());
        menuItemResponse.setPrice(menuItem.getPrice());

        return menuItemResponse;
    }
}
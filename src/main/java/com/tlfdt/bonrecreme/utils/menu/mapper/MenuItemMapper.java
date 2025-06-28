package com.tlfdt.bonrecreme.utils.menu.mapper;

import com.tlfdt.bonrecreme.controller.api.v1.manager.dto.menuitems.MenuItemRequestDTO;
import com.tlfdt.bonrecreme.controller.api.v1.manager.dto.menuitems.MenuItemResponseDTO;
import com.tlfdt.bonrecreme.model.restaurant.MenuItem;
import org.springframework.stereotype.Component;

@Component
public class MenuItemMapper {

    /**
     * Converts a menuitems entity to a MenuItemResponseDTO.
     *
     * @param menuItem The menuitems entity.
     * @return The corresponding MenuItemResponseDTO.
     */
    public MenuItemResponseDTO toResponseDTO(MenuItem menuItem) {
        return MenuItemResponseDTO.fromMenuItem(menuItem);
    }

    /**
     * Creates a new menuitems entity from a MenuItemRequestDTO.
     *
     * @param requestDTO The DTO containing the data for the new item.
     * @return A new menuitems entity.
     */
    public MenuItem toNewEntity(MenuItemRequestDTO requestDTO) {
        return MenuItem.builder()
                .name(requestDTO.getName())
                .description(requestDTO.getDescription())
                .price(requestDTO.getPrice())
                .build();
    }

    /**
     * Updates an existing menuitems entity with data from a MenuItemRequestDTO.
     *
     * @param menuItem   The existing entity to update.
     * @param requestDTO The DTO containing the new data.
     */
    public void updateEntityFromDTO(MenuItem menuItem, MenuItemRequestDTO requestDTO) {
        menuItem.setName(requestDTO.getName());
        menuItem.setDescription(requestDTO.getDescription());
        menuItem.setPrice(requestDTO.getPrice());
    }
}

package com.tlfdt.bonrecreme.controller.api.v1.customer.dto;

import com.tlfdt.bonrecreme.model.restaurant.MenuItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuResponseDTO implements Serializable {
    private List<MenuItemDTO> menuItems;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuItemDTO implements Serializable{
        private Long id;
        private String name;
        private String description;
        private BigDecimal price;
    }

    /**
     * Convert a list of MenuItem entities to a MenuResponseDTO
     * @param menuItems List of MenuItem entities
     * @return MenuResponseDTO containing MenuItemDTOs
     */
    public static MenuResponseDTO fromMenuItems(List<MenuItem> menuItems) {
        List<MenuItemDTO> menuItemDTOs = menuItems.stream()
                .map(menuItem -> {
                    MenuItemDTO dto = new MenuItemDTO();
                    dto.setId(menuItem.getId());
                    dto.setName(menuItem.getName());
                    dto.setDescription(menuItem.getDescription());
                    dto.setPrice(menuItem.getPrice());
                    return dto;
                })
                .collect(Collectors.toList());

        return new MenuResponseDTO(menuItemDTOs);
    }
}

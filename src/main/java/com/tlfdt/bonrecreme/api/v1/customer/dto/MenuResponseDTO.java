package com.tlfdt.bonrecreme.api.v1.customer.dto;

import com.tlfdt.bonrecreme.model.restaurant.MenuItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuResponseDTO {
    private List<MenuItemDTO> menuItems;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuItemDTO {
        private Long id;
        private String name;
        private String description;
        private BigDecimal price;
        private String category;
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
                    dto.setCategory(menuItem.getCategory() != null ? menuItem.getCategory().getName() : null);
                    return dto;
                })
                .collect(Collectors.toList());

        return new MenuResponseDTO(menuItemDTOs);
    }
}

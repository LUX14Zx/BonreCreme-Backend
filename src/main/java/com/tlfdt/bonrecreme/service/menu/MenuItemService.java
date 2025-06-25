package com.tlfdt.bonrecreme.service.menu;

import com.tlfdt.bonrecreme.api.v1.manager.dto.MenuItemRequestDTO;
import com.tlfdt.bonrecreme.api.v1.manager.dto.MenuItemResponseDTO;
import java.util.List;

public interface MenuItemService {
    MenuItemResponseDTO createMenuItem(MenuItemRequestDTO requestDTO);
    MenuItemResponseDTO getMenuItemById(Long id);
    List<MenuItemResponseDTO> getAllMenuItems();
    MenuItemResponseDTO updateMenuItem(Long id, MenuItemRequestDTO requestDTO);
    void deleteMenuItem(Long id);
}
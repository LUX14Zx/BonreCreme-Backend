package com.tlfdt.bonrecreme.api.v1.manager.usecase;

import com.tlfdt.bonrecreme.api.v1.dto.ApiResponseDTO;
import com.tlfdt.bonrecreme.api.v1.manager.dto.MenuItem.MenuItemRequestDTO;
import com.tlfdt.bonrecreme.api.v1.manager.dto.MenuItem.MenuItemResponseDTO;
import com.tlfdt.bonrecreme.service.menu.MenuItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/manager/menu-items")
@RequiredArgsConstructor
public class MenuItemController {

    private final MenuItemService menuItemService;

    @PostMapping
    public ResponseEntity<ApiResponseDTO<MenuItemResponseDTO>> createMenuItem(@RequestBody MenuItemRequestDTO requestDTO) {
        MenuItemResponseDTO createdMenuItem = menuItemService.createMenuItem(requestDTO);
        ApiResponseDTO<MenuItemResponseDTO> response = ApiResponseDTO.<MenuItemResponseDTO>builder()
                .api_data(createdMenuItem)
                .status("success")
                .message("Menu item created successfully")
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<MenuItemResponseDTO>> getMenuItemById(@PathVariable Long id) {
        MenuItemResponseDTO menuItem = menuItemService.getMenuItemById(id);
        ApiResponseDTO<MenuItemResponseDTO> response = ApiResponseDTO.<MenuItemResponseDTO>builder()
                .api_data(menuItem)
                .status("success")
                .message("Menu item fetched successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<MenuItemResponseDTO>>> getAllMenuItems() {
        List<MenuItemResponseDTO> menuItems = menuItemService.getAllMenuItems();
        ApiResponseDTO<List<MenuItemResponseDTO>> response = ApiResponseDTO.<List<MenuItemResponseDTO>>builder()
                .api_data(menuItems)
                .status("success")
                .message("All menu items fetched successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<MenuItemResponseDTO>> updateMenuItem(@PathVariable Long id, @RequestBody MenuItemRequestDTO requestDTO) {
        MenuItemResponseDTO updatedMenuItem = menuItemService.updateMenuItem(id, requestDTO);
        ApiResponseDTO<MenuItemResponseDTO> response = ApiResponseDTO.<MenuItemResponseDTO>builder()
                .api_data(updatedMenuItem)
                .status("success")
                .message("Menu item updated successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteMenuItem(@PathVariable Long id) {
        menuItemService.deleteMenuItem(id);
        ApiResponseDTO<Void> response = ApiResponseDTO.<Void> builder()
                .status("success")
                .message("Menu item deleted successfully")
                .build();
        return ResponseEntity.ok(response);
    }
}
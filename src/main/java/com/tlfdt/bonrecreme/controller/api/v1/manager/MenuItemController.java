package com.tlfdt.bonrecreme.controller.api.v1.manager;

import com.tlfdt.bonrecreme.controller.api.v1.dto.ApiResponseDTO;
import com.tlfdt.bonrecreme.controller.api.v1.manager.dto.menuitems.MenuItemRequestDTO;
import com.tlfdt.bonrecreme.controller.api.v1.manager.dto.menuitems.MenuItemResponseDTO;
import com.tlfdt.bonrecreme.service.menu.MenuItemService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * REST controller for managing menu items.
 * Provides endpoints for creating, retrieving, updating, and deleting menu items.
 * All inputs are validated to ensure data integrity.
 */
@RestController
@RequestMapping("/api/v1/manager/menu-items")
@RequiredArgsConstructor
@Validated // Enables validation for path variables and request parameters.
public class MenuItemController {

    private final MenuItemService menuItemService;

    /**
     * Creates a new menu item.
     *
     * @param requestDTO The DTO containing the details of the menu item to create. Must be valid.
     * @return A standardized API response containing the details of the created menu item.
     */
    @PostMapping
    public ResponseEntity<ApiResponseDTO<MenuItemResponseDTO>> createMenuItem(
            @Valid @RequestBody MenuItemRequestDTO requestDTO) {
        MenuItemResponseDTO createdMenuItem = menuItemService.createMenuItem(requestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(createdMenuItem, "Menu item created successfully."));
    }

    /**
     * Retrieves a specific menu item by its ID.
     *
     * @param id The unique identifier of the menu item. Must be a positive number.
     * @return A standardized API response containing the details of the fetched menu item.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<MenuItemResponseDTO>> getMenuItemById(
            @PathVariable @Positive(message = "Menu item ID must be a positive number.") Long id) {
        MenuItemResponseDTO menuItem = menuItemService.getMenuItemById(id);
        return ResponseEntity.ok(ApiResponseDTO.success(menuItem, "Menu item fetched successfully."));
    }

    /**
     * Retrieves a paginated list of all menu items.
     *
     * @return A standardized API response containing a page of all menu items.
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<MenuItemResponseDTO>>> getAllMenuItems() {
        List<MenuItemResponseDTO> menuItemsList = menuItemService.getAllMenuItems();
        return ResponseEntity.ok(ApiResponseDTO.success(menuItemsList, "All menu items fetched successfully."));
    }

    /**
     * Updates an existing menu item.
     *
     * @param id         The unique identifier of the menu item to update. Must be a positive number.
     * @param requestDTO The DTO containing the updated details. Must be valid.
     * @return A standardized API response containing the details of the updated menu item.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<MenuItemResponseDTO>> updateMenuItem(
            @PathVariable @Positive(message = "Menu item ID must be a positive number.") Long id,
            @Valid @RequestBody MenuItemRequestDTO requestDTO) {
        MenuItemResponseDTO updatedMenuItem = menuItemService.updateMenuItem(id, requestDTO);
        return ResponseEntity.ok(ApiResponseDTO.success(updatedMenuItem, "Menu item updated successfully."));
    }

    /**
     * Deletes a menu item by its ID.
     *
     * @param id The unique identifier of the menu item to delete. Must be a positive number.
     * @return A standardized API response confirming the deletion.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteMenuItem(
            @PathVariable @Positive(message = "Menu item ID must be a positive number.") Long id) {
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.ok(ApiResponseDTO.success(null, "Menu item deleted successfully."));
    }
}

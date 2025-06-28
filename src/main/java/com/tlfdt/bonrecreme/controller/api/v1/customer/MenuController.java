package com.tlfdt.bonrecreme.controller.api.v1.customer;

import com.tlfdt.bonrecreme.controller.api.v1.dto.ApiResponseDTO;
import com.tlfdt.bonrecreme.controller.api.v1.manager.dto.MenuItem.MenuItemResponseDTO;
import com.tlfdt.bonrecreme.service.menu.MenuItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for customer-facing menu operations.
 * Provides endpoints for customers to view available menu items.
 */
@RestController
@RequestMapping("/api/v1/customer/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuItemService menuItemService;

    /**
     * Fetches a list of all currently available menu items.
     *
     * @return A ResponseEntity containing a standardized API response with a List of menu items.
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<MenuItemResponseDTO>>> getAllMenuItems() {
        // Pass the pageable object to the service layer
        List<MenuItemResponseDTO> menuItemList = menuItemService.getAllMenuItems();

        // Correctly use the 'data' field of the builder
        ApiResponseDTO<List<MenuItemResponseDTO>> response = ApiResponseDTO.<List<MenuItemResponseDTO>>builder()
                .data(menuItemList)
                .status("success")
                .message("Menu items fetched successfully")
                .build();

        return ResponseEntity.ok(response);
    }
}
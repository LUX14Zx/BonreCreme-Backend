package com.tlfdt.bonrecreme.controller.api.v1.customer;

import com.tlfdt.bonrecreme.controller.api.v1.dto.ApiResponseDTO;

import com.tlfdt.bonrecreme.controller.api.v1.manager.dto.MenuItem.MenuItemResponseDTO;
import com.tlfdt.bonrecreme.service.menu.MenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/customer/menu")
public class MenuUsecase {

    private final MenuItemService menuItemService;

    @Autowired
    public MenuUsecase(MenuItemService menuService, MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    /**
     * Fetch all menu items
     *
     * @return List of all menu items
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<MenuItemResponseDTO>>> getAllMenuItems() {

        List<MenuItemResponseDTO> menuItemResponse = menuItemService.getAllMenuItems();

        ApiResponseDTO<List<MenuItemResponseDTO>> response = ApiResponseDTO.<List<MenuItemResponseDTO>>builder()
                .api_data(menuItemResponse)
                .status("success")
                .message("Menu items fetched successfully")
                .build();

        return ResponseEntity.ok(response);
    }
}
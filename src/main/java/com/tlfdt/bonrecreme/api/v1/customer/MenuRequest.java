package com.tlfdt.bonrecreme.api.v1.customer;

import com.tlfdt.bonrecreme.api.v1.customer.dto.MenuResponseDTO;
import com.tlfdt.bonrecreme.api.v1.dto.ApiResponseDTO;
import com.tlfdt.bonrecreme.model.restaurant.MenuItem;

import com.tlfdt.bonrecreme.repository.restaurant.MenuItemRepository;
import com.tlfdt.bonrecreme.repository.restaurant.OrderItemRepository;
import com.tlfdt.bonrecreme.repository.restaurant.OrderRepository;
import com.tlfdt.bonrecreme.repository.restaurant.SeatTableRepository;
import com.tlfdt.bonrecreme.service.menu.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/customer/menu")
public class MenuRequest {

    private final MenuService menuService;

    @Autowired
    public MenuRequest(MenuService menuService) {
        this.menuService = menuService;
    }

    /**
     * Fetch all menu items
     * @return List of all menu items
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<MenuResponseDTO>> getAllMenuItems() {

        MenuResponseDTO menuResponseDTO = menuService.getAllMenuItems();

        ApiResponseDTO<MenuResponseDTO> response = ApiResponseDTO.<MenuResponseDTO>builder()
                .api_data(menuResponseDTO)
                .status("success")
                .message("Menu items fetched successfully")
                .build();

        return ResponseEntity.ok(response);
    }
}
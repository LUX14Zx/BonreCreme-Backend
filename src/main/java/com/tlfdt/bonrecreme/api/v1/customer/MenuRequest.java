package com.tlfdt.bonrecreme.api.v1.customer;

import com.tlfdt.bonrecreme.api.v1.customer.dto.MenuRequestDTO;
import com.tlfdt.bonrecreme.api.v1.customer.dto.MenuResponseDTO;
import com.tlfdt.bonrecreme.api.v1.dto.ApiResponseDTO;
import com.tlfdt.bonrecreme.model.restaurant.MenuItem;
import com.tlfdt.bonrecreme.model.restaurant.Order;
import com.tlfdt.bonrecreme.model.restaurant.OrderItem;
import com.tlfdt.bonrecreme.model.restaurant.RestaurantTable;
import com.tlfdt.bonrecreme.model.restaurant.enums.OrderStatus;
import com.tlfdt.bonrecreme.repository.restaurant.MenuItemRepository;
import com.tlfdt.bonrecreme.repository.restaurant.OrderItemRepository;
import com.tlfdt.bonrecreme.repository.restaurant.OrderRepository;
import com.tlfdt.bonrecreme.repository.restaurant.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/customer/menu")
public class MenuRequest {

    private final MenuItemRepository menuItemRepository;

    @Autowired
    public MenuRequest(MenuItemRepository menuItemRepository, 
                      OrderRepository orderRepository, 
                      OrderItemRepository orderItemRepository,
                      TableRepository tableRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    /**
     * Fetch all menu items
     * @return List of all menu items
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<MenuResponseDTO>> getAllMenuItems() {
        List<MenuItem> menuItems = menuItemRepository.findAll();
        MenuResponseDTO menuResponseDTO = MenuResponseDTO.fromMenuItems(menuItems);

        ApiResponseDTO<MenuResponseDTO> response = ApiResponseDTO.<MenuResponseDTO>builder()
                .api_data(menuResponseDTO)
                .status("success")
                .message("Menu items fetched successfully")
                .build();

        return ResponseEntity.ok(response);
    }
}

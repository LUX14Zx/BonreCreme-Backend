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
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final TableRepository tableRepository;

    @Autowired
    public MenuRequest(MenuItemRepository menuItemRepository, 
                      OrderRepository orderRepository, 
                      OrderItemRepository orderItemRepository,
                      TableRepository tableRepository) {
        this.menuItemRepository = menuItemRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.tableRepository = tableRepository;
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

    /**
     * Create a new order with order items
     * @param menuRequestDTO The order request containing table ID and order items
     * @return The created order
     */
    @PostMapping("/order")
    public ResponseEntity<ApiResponseDTO<Order>> createOrder(@RequestBody MenuRequestDTO menuRequestDTO) {
        // Validate table exists
        Optional<RestaurantTable> tableOptional = tableRepository.findById(menuRequestDTO.getTableId());
        if (tableOptional.isEmpty()) {
            ApiResponseDTO<Order> response = ApiResponseDTO.<Order>builder()
                    .status("error")
                    .message("Table not found")
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // Create new order
        Order order = new Order();
        order.setRestaurantTable(tableOptional.get());
        order.setStatus(OrderStatus.BILLED);
        order = orderRepository.save(order);

        // Create order items
        for (MenuRequestDTO.OrderItemRequest itemRequest : menuRequestDTO.getItems()) {
            Optional<MenuItem> menuItemOptional = menuItemRepository.findById(itemRequest.getMenuItemId());
            if (menuItemOptional.isPresent()) {
                MenuItem menuItem = menuItemOptional.get();

                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setMenuItem(menuItem);
                orderItem.setQuantity(itemRequest.getQuantity());
                orderItem.setPrice(menuItem.getPrice());
                orderItem.setSpecialRequests(itemRequest.getSpecialRequests());

                orderItemRepository.save(orderItem);
            }
        }

        // Refresh order to get updated order items
        order = orderRepository.findById(order.getId()).orElse(order);

        ApiResponseDTO<Order> response = ApiResponseDTO.<Order>builder()
                .api_data(order)
                .status("success")
                .message("Order created successfully")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

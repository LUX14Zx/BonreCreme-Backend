package com.tlfdt.bonrecreme.service.customer;

import com.tlfdt.bonrecreme.api.v1.customer.dto.MenuRequestDTO;
import com.tlfdt.bonrecreme.model.restaurant.MenuItem;
import com.tlfdt.bonrecreme.model.restaurant.Order;
import com.tlfdt.bonrecreme.model.restaurant.OrderItem;
import com.tlfdt.bonrecreme.model.restaurant.RestaurantTable;
import com.tlfdt.bonrecreme.model.restaurant.enums.OrderStatus;
import com.tlfdt.bonrecreme.repository.restaurant.MenuItemRepository;
import com.tlfdt.bonrecreme.repository.restaurant.OrderItemRepository;
import com.tlfdt.bonrecreme.repository.restaurant.OrderRepository;
import com.tlfdt.bonrecreme.repository.restaurant.TableRepository;
import com.tlfdt.bonrecreme.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final MenuItemRepository menuItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final TableRepository tableRepository;

    /**
     * Creates a new order, including finding the table, creating the order items,
     * and saving everything to the database within a single transaction.
     *
     * @param menuRequestDTO DTO with order details.
     * @return The complete Order object with all its items.
     */
    @Override
    @Transactional("restaurantTransactionManager")
    public Order createOrder(MenuRequestDTO menuRequestDTO) {
        // Step 1: Validate that the table exists. Throws ResourceNotFoundException if not found.
        Optional<RestaurantTable> table = tableRepository.findById(menuRequestDTO.getTableId());

        Order order = new Order();
        if (table.isPresent())
        {
            order.setRestaurantTable(table.get());
            order.setStatus(OrderStatus.NEW);

        }
        Order savedOrder = orderRepository.save(order);

        // Step 3: Process and create the list of OrderItem entities.
        List<OrderItem> orderItems = menuRequestDTO.getItems().stream()
                .map(itemRequest -> {
                    // Find the corresponding menu item. Throws ResourceNotFoundException if not found.
                    Optional<MenuItem> menuItem = menuItemRepository.findById(itemRequest.getMenuItemId());

                    // Create the order item and link it to the parent order and menu item.
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(savedOrder);
                    orderItem.setMenuItem(menuItem.get());
                    orderItem.setQuantity(itemRequest.getQuantity());
                    orderItem.setSpecialRequests(itemRequest.getSpecialRequests());
                    return orderItem;
                })
                .collect(Collectors.toList());

        // Step 4: Batch save all order items for efficiency.
        orderItemRepository.saveAll(orderItems);

        // Step 5: Set the saved items back to the order object to return the complete entity.
        savedOrder.setOrderItems(orderItems);

        return savedOrder;
    }
}

package com.tlfdt.bonrecreme.service.order;

import com.tlfdt.bonrecreme.controller.api.v1.customer.dto.MenuRequestDTO;
import com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto.UpdateOrderRequestDTO;
import com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto.UpdateOrderStatusRequestDTO;
import com.tlfdt.bonrecreme.model.restaurant.Order;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service interface for handling order-related business logic.
 */
public interface OrderService {

    /**
     * Creates a new order based on the provided request data.
     *
     * @param menuRequestDTO DTO containing the table ID and a list of items to order.
     * @return The newly created Order entity.
     */
    Order createOrder(MenuRequestDTO menuRequestDTO);
    Order updateOrder(Long orderId, UpdateOrderRequestDTO requestDTO);
    Order updateOrderStatus(Long orderId, UpdateOrderStatusRequestDTO requestDTO);
    Order updateOrderStatusReadyToServe(Long orderId);
    Order serveOrder(Long orderId);

}

package com.tlfdt.bonrecreme.service.order;

import com.tlfdt.bonrecreme.controller.api.v1.customer.dto.MenuRequestDTO;
import com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto.OrderNotificationDTO;
import com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto.UpdateOrderRequestDTO;
import com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto.UpdateOrderStatusRequestDTO;
import com.tlfdt.bonrecreme.model.restaurant.Bill;
import com.tlfdt.bonrecreme.model.restaurant.Order;

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

    OrderNotificationDTO createOrder(MenuRequestDTO menuRequestDTO);
    OrderNotificationDTO updateOrder(Long orderId, UpdateOrderRequestDTO requestDTO);
    OrderNotificationDTO updateOrderStatus(Long orderId, UpdateOrderStatusRequestDTO requestDTO);
    OrderNotificationDTO updateOrderStatusReadyToServe(Long orderId);
    OrderNotificationDTO serveOrder(Long orderId);
    Bill checkoutBillTable(Long tableId);

}

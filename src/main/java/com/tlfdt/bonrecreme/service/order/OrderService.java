package com.tlfdt.bonrecreme.service.order;

import com.tlfdt.bonrecreme.controller.api.v1.customer.dto.MenuRequestDTO;
import com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto.OrderNotificationDTO;
import com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto.UpdateOrderRequestDTO;
import com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto.UpdateOrderStatusRequestDTO;
import com.tlfdt.bonrecreme.model.restaurant.enums.OrderStatus;

/**
 * Service interface for handling order-related business logic.
 *
 * This service defines the contract for creating, updating, and managing
 * the lifecycle of customer orders.
 */
public interface OrderService {

    /**
     * Creates a new order based on the provided request data. This action
     * typically triggers a notification to the kitchen.
     *
     * @param menuRequestDTO DTO containing the table ID and a list of items to order.
     * @return An {@link OrderNotificationDTO} for real-time client updates.
     */
    OrderNotificationDTO createOrder(MenuRequestDTO menuRequestDTO);

    /**
     * Updates the items of an existing order. This is typically done by a customer
     * before the order preparation has started.
     *
     * @param orderId    The unique identifier of the order to update.
     * @param requestDTO A DTO containing the complete new list of items for the order.
     * @return An {@link OrderNotificationDTO} reflecting the updated order.
     */
    OrderNotificationDTO updateOrderItems(Long orderId, UpdateOrderRequestDTO requestDTO);

    /**
     * Updates the status of an existing order. This is a general-purpose method
     * typically used by the kitchen staff to advance the order's state (e.g., to PREPARING).
     *
     * @param orderId    The unique identifier of the order.
     * @param requestDTO A DTO containing the new {@link OrderStatus}.
     * @return An {@link OrderNotificationDTO} reflecting the new status.
     */
    OrderNotificationDTO updateOrderStatus(Long orderId, UpdateOrderStatusRequestDTO requestDTO);

    /**
     * Marks a specific order as prepared and ready to be delivered to the table.
     * This action notifies the waitstaff.
     *
     * @param orderId The unique identifier of the order that is ready.
     * @return An {@link OrderNotificationDTO} reflecting the 'READY_TO_SERVE' status.
     */
    OrderNotificationDTO markOrderAsReadyToServe(Long orderId);

    /**
     * Confirms that an order has been successfully delivered to the customer's table.
     * This is the final step in the serving process, performed by waitstaff.
     *
     * @param orderId The unique identifier of the order that has been served.
     * @return An {@link OrderNotificationDTO} reflecting the 'SERVED' status.
     */
    OrderNotificationDTO markOrderAsServed(Long orderId);
}
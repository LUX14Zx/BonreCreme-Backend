package com.tlfdt.bonrecreme.service;

import com.tlfdt.bonrecreme.api.v1.customer.dto.MenuRequestDTO;
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
    Order createOrder(MenuRequestDTO menuRequestDTO);
}

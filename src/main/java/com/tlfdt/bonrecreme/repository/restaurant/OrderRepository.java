package com.tlfdt.bonrecreme.repository.restaurant;

import com.tlfdt.bonrecreme.model.restaurant.Order;
import com.tlfdt.bonrecreme.model.restaurant.RestaurantTable;
import com.tlfdt.bonrecreme.model.restaurant.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @NonNull
    Optional<Order> findById(@NonNull Long id);

    boolean existsById(@NonNull Long id);
    
    List<Order> findByStatus(OrderStatus status);
    
    List<Order> findByRestaurantTable(RestaurantTable restaurantTable);
    
    List<Order> findByTotalAmountGreaterThan(BigDecimal amount);
    
    List<Order> findByRestaurantTableAndStatus(RestaurantTable restaurantTable, OrderStatus status);
}
package com.tlfdt.bonrecreme.repository.restaurant;

import com.tlfdt.bonrecreme.model.restaurant.MenuItem;
import com.tlfdt.bonrecreme.model.restaurant.Order;
import com.tlfdt.bonrecreme.model.restaurant.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @NonNull
    Optional<OrderItem> findById(@NonNull Long id);

    boolean existsById(@NonNull Long id);
    
    List<OrderItem> findByOrder(Order order);
    
    List<OrderItem> findByMenuItem(MenuItem menuItem);
    
    List<OrderItem> findByQuantityGreaterThan(Integer quantity);
    
    List<OrderItem> findByOrderAndMenuItem(Order order, MenuItem menuItem);
}
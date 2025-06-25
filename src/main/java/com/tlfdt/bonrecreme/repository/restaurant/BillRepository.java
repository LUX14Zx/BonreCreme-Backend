package com.tlfdt.bonrecreme.repository.restaurant;

import com.tlfdt.bonrecreme.model.restaurant.Bill;
import com.tlfdt.bonrecreme.model.restaurant.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    @NonNull
    Optional<Bill> findById(@NonNull Long id);

    boolean existsById(@NonNull Long id);
    
    Optional<Bill> findByOrder(Order order);
    
    List<Bill> findByPaymentStatus(String paymentStatus);
    
    List<Bill> findByPaymentMethod(String paymentMethod);
}
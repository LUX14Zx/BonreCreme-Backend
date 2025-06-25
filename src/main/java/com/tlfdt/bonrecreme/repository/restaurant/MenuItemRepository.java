package com.tlfdt.bonrecreme.repository.restaurant;

import com.tlfdt.bonrecreme.model.restaurant.Category;
import com.tlfdt.bonrecreme.model.restaurant.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    @NonNull
    Optional<MenuItem> findById(@NonNull Long id);

    boolean existsById(@NonNull Long id);
    
    Optional<MenuItem> findByName(String name);
    
    boolean existsByName(String name);
    
    List<MenuItem> findByCategory(Category category);
    
    List<MenuItem> findByPriceLessThan(BigDecimal price);
    
    List<MenuItem> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
}
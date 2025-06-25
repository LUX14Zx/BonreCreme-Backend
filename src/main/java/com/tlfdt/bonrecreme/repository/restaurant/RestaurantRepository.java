package com.tlfdt.bonrecreme.repository.restaurant;

import com.tlfdt.bonrecreme.model.restaurant.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    @NonNull
    Optional<Restaurant> findById(@NonNull Long id);

    boolean existsById(@NonNull Long id);
    
    Optional<Restaurant> findByName(String name);
    
    boolean existsByName(String name);
}
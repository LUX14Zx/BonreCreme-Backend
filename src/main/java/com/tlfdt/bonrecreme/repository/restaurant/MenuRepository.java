package com.tlfdt.bonrecreme.repository.restaurant;

import com.tlfdt.bonrecreme.model.restaurant.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    @NonNull
    Optional<Menu> findById(@NonNull Long id);

    boolean existsById(@NonNull Long id);
    
    Optional<Menu> findByName(String name);
    
    boolean existsByName(String name);
}
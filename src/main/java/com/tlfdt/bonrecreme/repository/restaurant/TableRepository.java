package com.tlfdt.bonrecreme.repository.restaurant;

import com.tlfdt.bonrecreme.model.restaurant.RestaurantTable;
import com.tlfdt.bonrecreme.model.restaurant.enums.TableStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface TableRepository extends JpaRepository<RestaurantTable, Long> {


    @NonNull
    Optional<RestaurantTable> findById(@NonNull Long id);

    boolean existsById(@NonNull Long id);

    /**
     * Find all tables with a specific status
     * @param status the table status to search for
     * @return list of tables with the specified status
     */
    @NonNull
    List<RestaurantTable> findByStatus(@NonNull TableStatus status);
}

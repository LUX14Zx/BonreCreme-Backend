package com.tlfdt.bonrecreme.repository.restaurant;

import com.tlfdt.bonrecreme.model.restaurant.SeatTable;
import com.tlfdt.bonrecreme.model.restaurant.enums.TableStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface SeatTableRepository extends JpaRepository<SeatTable, Long> {


    @NonNull
    Optional<SeatTable> findById(@NonNull Long id);

    boolean existsById(@NonNull Long id);

    /**
     * Find all tables with a specific status
     * @param status the table status to search for
     * @return list of tables with the specified status
     */
    @NonNull
    List<SeatTable> findByStatus(@NonNull TableStatus status);
}

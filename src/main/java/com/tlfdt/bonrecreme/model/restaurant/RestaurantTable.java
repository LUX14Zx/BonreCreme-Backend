package com.tlfdt.bonrecreme.model.restaurant;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.tlfdt.bonrecreme.model.restaurant.enums.TableStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * Represents a physical table in the restaurant.
 * Renamed to RestaurantTable to avoid conflict with the @customer annotation.
 */
@Entity
@Table(name = "Tables")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "table_id")
    private Long id;

    @Column(name = "table_number", nullable = false)
    private Integer tableNumber;

    @Column(name = "seating_capacity", nullable = false)
    private Integer seatingCapacity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TableStatus status;

    @OneToMany(mappedBy = "restaurantTable", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference // Parent side: Will be serialized
    private List<Order> orders;
}

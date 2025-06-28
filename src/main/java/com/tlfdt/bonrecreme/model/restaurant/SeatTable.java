package com.tlfdt.bonrecreme.model.restaurant;

import com.tlfdt.bonrecreme.model.restaurant.enums.TableStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a physical table in the restaurant where customers can be seated.
 */
@Entity
@Table(name = "seat_tables")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@ToString(exclude = "orders")
public class SeatTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_number", nullable = false, unique = true)
    private Integer tableNumber;

    @Column(name = "seating_capacity", nullable = false)
    private Integer seatingCapacity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TableStatus status;

    @OneToMany(
            mappedBy = "seatTable",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<Order> orders = new ArrayList<>();

    // == Helper methods for managing the bidirectional relationship ==

    public void addOrder(Order order) {
        this.orders.add(order);
        order.setSeatTable(this);
    }

    public void removeOrder(Order order) {
        this.orders.remove(order);
        order.setSeatTable(null);
    }

    // == Custom equals and hashCode for safe use in collections ==

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeatTable seatTable = (SeatTable) o;
        // Business key for new entities, ID for persisted entities.
        return id != null ? id.equals(seatTable.id) : Objects.equals(tableNumber, seatTable.tableNumber);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : Objects.hash(tableNumber);
    }
}

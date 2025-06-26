package com.tlfdt.bonrecreme.model.restaurant;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.tlfdt.bonrecreme.model.restaurant.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    /**
     * The unique identifier for the order.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The table where the order was placed.
     * The 'referencedColumnName' is explicitly set to 'id' to resolve potential mapping ambiguity.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_table_id", referencedColumnName = "table_id")
    private SeatTable seatTable;

    /**
     * The bill that this order is a part of. This can be null if the order has not been billed yet.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_id")
    private Bill bill;

    /**
     * The list of items included in this order.
     * 'orphanRemoval = true' ensures that if an OrderItem is removed from this list, it's also deleted from the database.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    /**
     * The current status of the order (e.g., PENDING, SERVED).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;
}
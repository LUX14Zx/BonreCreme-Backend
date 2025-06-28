package com.tlfdt.bonrecreme.model.restaurant;

import com.tlfdt.bonrecreme.model.restaurant.enums.BillStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a customer's bill, which aggregates one or more orders for payment.
 * This entity is designed to be robust, avoiding common JPA pitfalls with Lombok.
 */
@Entity
@Table(name = "bills")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Required for JPA
@AllArgsConstructor(access = AccessLevel.PRIVATE) // Required for @Builder
@Builder
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_table_id", referencedColumnName = "id")
    private SeatTable seatTable;

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default // Ensures the list is initialized when using the builder
    private List<Order> orders = new ArrayList<>();

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BillStatus status;

    @Column(name = "payment_method")
    private String paymentMethod;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    // == Helper methods for managing the bidirectional relationship ==

    /**
     * Adds an order to this bill and sets the back-reference on the order.
     * @param order The order to add.
     */
    public void addOrder(Order order) {
        this.orders.add(order);
        order.setBill(this);
    }

    /**
     * Removes an order from this bill and clears the back-reference on the order.
     * @param order The order to remove.
     */
    public void removeOrder(Order order) {
        this.orders.remove(order);
        order.setBill(null);
    }

    // == Custom equals and hashCode for safe use in collections ==

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bill bill = (Bill) o;
        // A reliable equality check for JPA entities.
        return id != null ? id.equals(bill.id) : Objects.equals(createdAt, bill.createdAt) && Objects.equals(seatTable, bill.seatTable);
    }

    @Override
    public int hashCode() {
        // A reliable hashCode implementation for JPA entities.
        return id != null ? id.hashCode() : getClass().hashCode();
    }
}

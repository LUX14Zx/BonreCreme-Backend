package com.tlfdt.bonrecreme.model.restaurant;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.tlfdt.bonrecreme.model.restaurant.enums.BillStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


/**
 * Represents a bill for a specific table, containing multiple orders.
 */
@Entity
@Table(name = "bills")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bill {

    /**
     * The unique identifier for the bill.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The table to which this bill belongs.
     * The 'referencedColumnName' is explicitly set to 'id' to resolve potential mapping ambiguity.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_table_id", referencedColumnName = "table_id")
    private SeatTable seatTable;

    /**
     * The list of orders included in this bill.
     * The 'mappedBy' attribute indicates that the Order entity owns the relationship.
     */
    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL)
    private List<Order> orders;

    /**
     * The total calculated amount for the bill.
     */
    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    /**
     * The current status of the bill (e.g., UNPAID, PAID).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BillStatus status;

    @Column String paymentMethod;
}
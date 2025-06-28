package com.tlfdt.bonrecreme.model.restaurant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * Represents the lifecycle status of a customer's order.
 * Each status has a user-friendly display name and is designed for safe
 * serialization and deserialization.
 */
@Getter
public enum OrderStatus {

    /**
     * The initial state of an order after it has been placed by a customer
     * but before the kitchen has started preparing it.
     */
    PENDING("Pending"),

    /**
     * The order is actively being cooked. This is a new status we are adding.
     */
    COOKING("Cooking"),

    /**
     * The order is prepared and is ready to be picked up by waitstaff for serving.
     */
    READY_TO_SERVE("Ready to Serve"),

    /**
     * The order has been delivered to the customer's table by the waitstaff.
     */
    SERVED("Served"),

    /**
     * The order has been cancelled, either by the customer or staff.
     */
    CANCELLED("Cancelled"),

    /**
     * The order has been included in a finalized bill, but the bill has not yet been paid.
     */
    BILLED("Billed"),

    /**
     * The bill containing this order has been paid in full.
     */
    PAID("Paid");

    /**
     * A user-friendly representation of the status. This value is what will be
     * serialized to and deserialized from JSON.
     */
    @JsonValue
    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Deserializes a string value into an OrderStatus enum constant, case-insensitively.
     * This method is used by Jackson for converting incoming JSON strings to enums.
     *
     * @param displayName The string representation of the status (e.g., "Preparing").
     * @return The corresponding OrderStatus enum constant.
     * @throws IllegalArgumentException if the provided display name does not match any known status.
     */
    @JsonCreator
    public static OrderStatus fromDisplayName(String displayName) {
        if (displayName == null) {
            return null;
        }
        return Stream.of(OrderStatus.values())
                .filter(status -> status.displayName.equalsIgnoreCase(displayName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown OrderStatus: '" + displayName + "'"));
    }
}
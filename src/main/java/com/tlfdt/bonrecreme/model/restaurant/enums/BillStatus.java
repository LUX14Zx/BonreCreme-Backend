package com.tlfdt.bonrecreme.model.restaurant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * Represents the payment status of a customer's bill.
 * Each status has an associated display name for use in user interfaces and a
 * robust mapping from a string value.
 */
@Getter
public enum BillStatus {

    /**
     * The bill has been finalized and issued to the customer but has not yet been paid.
     */
    UNPAID("Unpaid"),

    PENDING("Pending"),
    /**
     * The bill has been successfully paid in full.
     */
    PAID("Paid");

    /**
     * A user-friendly representation of the status. This value is what will be
     * serialized to JSON.
     */
    @JsonValue
    private final String displayName;

    BillStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Deserializes a string value into a BillStatus enum constant, case-insensitively.
     * This method is used by Jackson for converting incoming JSON strings to enums.
     *
     * @param displayName The string representation of the status.
     * @return The corresponding BillStatus enum constant.
     * @throws IllegalArgumentException if the provided display name does not match any known status.
     */
    @JsonCreator
    public static BillStatus fromDisplayName(String displayName) {
        if (displayName == null) {
            return null;
        }
        return Stream.of(BillStatus.values())
                .filter(status -> status.displayName.equalsIgnoreCase(displayName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown BillStatus: '" + displayName + "'"));
    }
}

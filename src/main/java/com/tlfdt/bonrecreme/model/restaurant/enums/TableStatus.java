package com.tlfdt.bonrecreme.model.restaurant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * Represents the current status of a restaurant table.
 * Each status has a user-friendly display name and is designed for safe
 * serialization and deserialization.
 */
@Getter
public enum TableStatus {

    /**
     * The table is clean, set up, and ready to seat new customers.
     */
    AVAILABLE("Available"),

    /**
     * The table currently has customers seated at it.
     */
    OCCUPIED("Occupied"),

    /**
     * The table has been reserved by a customer for a specific time and is being held.
     */
    RESERVED("Reserved");

    /**
     * A user-friendly representation of the status. This value is what will be
     * serialized to and deserialized from JSON.
     */
    @JsonValue
    private final String displayName;

    TableStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Deserializes a string value into a TableStatus enum constant, case-insensitively.
     * This method is used by Jackson for converting incoming JSON strings to enums.
     *
     * @param displayName The string representation of the status (e.g., "Available").
     * @return The corresponding TableStatus enum constant.
     * @throws IllegalArgumentException if the provided display name does not match any known status.
     */
    @JsonCreator
    public static TableStatus fromDisplayName(String displayName) {
        if (displayName == null) {
            return null;
        }
        return Stream.of(TableStatus.values())
                .filter(status -> status.displayName.equalsIgnoreCase(displayName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown TableStatus: '" + displayName + "'"));
    }
}

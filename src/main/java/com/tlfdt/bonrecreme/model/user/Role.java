package com.tlfdt.bonrecreme.model.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.stream.Stream;

public enum Role {
    MANAGER("Manager");
    /**
     * A user-friendly representation of the status. This value is what will be
     * serialized to JSON.
     */
    @JsonValue
    private final String displayName;

    Role(String displayName) {
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
    public static Role fromDisplayName(String displayName) {
        if (displayName == null) {
            return null;
        }
        return Stream.of(Role.values())
                .filter(status -> status.displayName.equalsIgnoreCase(displayName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown Role: '" + displayName + "'"));
    }
}
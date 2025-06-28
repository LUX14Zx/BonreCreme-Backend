package com.tlfdt.bonrecreme.service.report.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * Defines the supported formats for generated sales reports.
 * This enum includes a display name and is designed for safe JSON serialization.
 */
@Getter
public enum ReportFormat {
    PDF("PDF"),
    CSV("CSV");

    /**
     * A user-friendly representation of the format. This value is what will be
     * serialized to and deserialized from JSON.
     */
    @JsonValue
    private final String displayName;

    ReportFormat(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Deserializes a string value into a ReportFormat enum constant, case-insensitively.
     * This method is used by Jackson for converting incoming JSON strings to enums.
     *
     * @param displayName The string representation of the format (e.g., "PDF").
     * @return The corresponding ReportFormat enum constant.
     * @throws IllegalArgumentException if the provided display name does not match any known format.
     */
    @JsonCreator
    public static ReportFormat fromDisplayName(String displayName) {
        if (displayName == null) {
            return null;
        }
        return Stream.of(ReportFormat.values())
                .filter(format -> format.displayName.equalsIgnoreCase(displayName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown ReportFormat: '" + displayName + "'"));
    }
}

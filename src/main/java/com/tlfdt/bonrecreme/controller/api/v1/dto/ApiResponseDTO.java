package com.tlfdt.bonrecreme.controller.api.v1.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * A standardized, immutable wrapper for all API responses in the application.
 * It provides a consistent structure containing the data payload, status,
 * a descriptive message, and a timestamp. Use the static factory methods
 * 'success' and 'error' for common cases, or the builder for more complex responses.
 *
 * @param <T> The type of the data payload being returned.
 */
@Value // Provides immutability, getters, toString, equals/hashCode, all-args constructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Don't serialize null fields (e.g., data on error)
public class ApiResponseDTO<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The main data payload of the response. The name in the JSON output will be "api_data".
     * This field will be omitted from the JSON if it is null.
     */
    @JsonProperty("api_data")
    T data;

    /**
     * The status of the response, typically "success" or "error".
     */
    String status;

    /**
     * A user-friendly message describing the result of the operation.
     */
    String message;

    /**
     * The timestamp when the API response was generated, in UTC.
     * This is set at creation time and formatted in ISO-8601 format with a 'Z' for UTC.
     */
    @Builder.Default
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    LocalDateTime timestamp = LocalDateTime.now(ZoneOffset.UTC);

    /**
     * Creates a standardized success response with a default message.
     *
     * @param data The payload to be returned.
     * @param <T> The type of the payload.
     * @return A pre-configured success ApiResponseDTO.
     */
    public static <T> ApiResponseDTO<T> success(String message) {
        return success(null, message);
    }

    /**
     * Creates a standardized success response with a custom message.
     *
     * @param data The payload to be returned.
     * @param message A custom success message.
     * @param <T> The type of the payload.
     * @return A pre-configured success ApiResponseDTO.
     */
    public static <T> ApiResponseDTO<T> success(T data, String message) {
        return ApiResponseDTO.<T>builder()
                .data(data)
                .status("success")
                .message(message)
                .build();
    }

    /**
     * Creates a standardized error response.
     * The data field will be null and not included in the JSON output.
     *
     * @param message A descriptive error message.
     * @return A pre-configured error ApiResponseDTO.
     */
    public static <T> ApiResponseDTO<T> error(String message) {
        return ApiResponseDTO.<T>builder()
                .status("error")
                .message(message)
                .build();
    }
}

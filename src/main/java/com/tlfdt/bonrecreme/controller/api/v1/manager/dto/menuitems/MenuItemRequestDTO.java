package com.tlfdt.bonrecreme.controller.api.v1.manager.dto.menuitems;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Represents an immutable and validated data transfer object for creating or updating a menu item.
 * This DTO is used as a request body in manager-related endpoints.
 */
@Value
public class MenuItemRequestDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The name of the menu item. Cannot be blank and must be between 3 and 100 characters.
     */
    @NotBlank(message = "Menu item name cannot be blank.")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters.")
    String name;

    /**
     * A description of the menu item. Limited to 255 characters. Can be null.
     */
    @Size(max = 255, message = "Description cannot exceed 255 characters.")
    String description;

    /**
     * The price of the menu item. Cannot be null and must not be negative.
     */
    @NotNull(message = "Price cannot be null.")
    @DecimalMin(value = "0.0", message = "Price must not be negative.")
    BigDecimal price;

    /**
     * A constructor for deserializing JSON into a MenuItemRequestDTO object.
     * The @JsonCreator annotation tells the JSON parser to use this constructor.
     * The @JsonProperty annotations map the JSON fields to the constructor parameters.
     */
    @JsonCreator
    public MenuItemRequestDTO(
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("price") BigDecimal price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }
}
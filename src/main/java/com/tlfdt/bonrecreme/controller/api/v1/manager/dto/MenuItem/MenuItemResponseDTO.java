package com.tlfdt.bonrecreme.controller.api.v1.manager.dto.MenuItem;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tlfdt.bonrecreme.model.restaurant.MenuItem;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Represents an immutable and validated data transfer object for a menu item response.
 * This DTO is used to send menu item details to clients.
 */
@Getter
@ToString
@EqualsAndHashCode
public class MenuItemResponseDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The unique identifier for the menu item. Must be a positive number.
     */
    @NotNull(message = "ID cannot be null.")
    @Positive(message = "ID must be a positive number.")
    @JsonProperty("id")
    private final Long id;

    /**
     * The name of the menu item. Cannot be blank.
     */
    @NotBlank(message = "Name cannot be blank.")
    @JsonProperty("name")
    private final String name;

    /**
     * A description of the menu item. Can be null.
     */
    @JsonProperty("description")
    private final String description;

    /**
     * The price of the menu item. Must not be null or negative.
     */
    @NotNull(message = "Price cannot be null.")
    @DecimalMin(value = "0.0", message = "Price must not be negative.")
    @JsonProperty("price")
    private final BigDecimal price;

    @JsonCreator
    public MenuItemResponseDTO(
            @JsonProperty("id") Long id,
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("price") BigDecimal price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    /**
     * Constructs a MenuItemResponseDTO from a MenuItem domain entity.
     * This encapsulates the mapping logic directly within the DTO.
     *
     * @param menuItem The MenuItem entity to convert.
     */
    public MenuItemResponseDTO(MenuItem menuItem) {
        this.id = menuItem.getId();
        this.name = menuItem.getName();
        this.description = menuItem.getDescription();
        this.price = menuItem.getPrice();
    }

    /**
     * A static factory method to create a MenuItemResponseDTO from a MenuItem entity.
     * This provides a clear and convenient way to perform the conversion.
     *
     * @param menuItem The source MenuItem entity.
     * @return A new, populated MenuItemResponseDTO.
     */
    public static MenuItemResponseDTO fromMenuItem(MenuItem menuItem) {
        if (menuItem == null) {
            return null;
        }
        return new MenuItemResponseDTO(menuItem);
    }
}
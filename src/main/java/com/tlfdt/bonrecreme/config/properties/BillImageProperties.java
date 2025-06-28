package com.tlfdt.bonrecreme.config.properties;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Type-safe configuration properties for bill image generation.
 * <p>
 * This class maps to properties under the "application.reports.bill-image" prefix
 * in the application.yml file, providing a structured and validated way to
 * manage settings.
 */
@Component
@ConfigurationProperties(prefix = "application.reports.bill-image")
@Data
@Validated // Enables validation on the properties themselves
public class BillImageProperties {

    /**
     * The width, in pixels, for the generated bill image.
     */
    @Positive(message = "Image width must be a positive number.")
    private int width = 300; // A sensible default value

    /**
     * The name of the Thymeleaf template to use for rendering the bill.
     */
    private String templateName = "bill-template";
}
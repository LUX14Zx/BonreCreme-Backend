package com.tlfdt.bonrecreme.exception;

import com.tlfdt.bonrecreme.controller.api.v1.dto.ApiResponseDTO;
import com.tlfdt.bonrecreme.exception.custom.CustomExceptionHandler;
import com.tlfdt.bonrecreme.exception.resource.ResourceNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.stream.Collectors;

/**
 * Global exception handler for the entire application.
 * This class uses @ControllerAdvice to intercept exceptions thrown by any controller
 * and formats them into a standardized ApiResponseDTO.
 */
@ControllerAdvice
@Slf4j
public class AppExceptionHandler {

    /**
     * Handles exceptions for when a resource is not found.
     * This allows us to return a more specific 404 Not Found status. It's placed
     * before the more generic CustomExceptionHandler to ensure it's caught first.
     *
     * @param ex The caught ResourceNotFoundException instance.
     * @return A ResponseEntity with a 404 Not Found status.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDTO.error(ex.getMessage()));
    }

    /**
     * Handles authentication failures, such as incorrect username or password.
     *
     * @param ex The caught BadCredentialsException instance.
     * @return A ResponseEntity with a 401 Unauthorized status.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleBadCredentialsException(BadCredentialsException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponseDTO.error("Authentication failed: Invalid credentials."));
    }

    /**
     * Handles custom business logic exceptions (e.g., resource not found, invalid state).
     * These are exceptions that we throw intentionally in the service layer.
     *
     * @param ex The caught CustomExceptionHandler instance.
     * @return A ResponseEntity with a 400 Bad Request status and a standardized error message.
     */
    @ExceptionHandler(CustomExceptionHandler.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleCustomException(CustomExceptionHandler ex) {
        log.warn("Business logic exception: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.error(ex.getMessage()));
    }

    /**
     * Handles validation exceptions for request parameters and path variables
     * (e.g., @Positive, @RequestParam @Validated).
     *
     * @param ex The caught ConstraintViolationException instance.
     * @return A ResponseEntity with a 400 Bad Request status and a detailed validation error message.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponseDTO<Object>> handleConstraintViolationException(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .collect(Collectors.joining(", "));
        log.warn("Constraint violation: {}", message);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.error("Validation failed: " + message));
    }

    /**
     * Handles validation exceptions for request bodies (@Valid annotation).
     * This provides detailed feedback on which fields of the DTO are invalid.
     *
     * @param ex The caught MethodArgumentNotValidException instance.
     * @return A ResponseEntity with a 400 Bad Request status and detailed field errors.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponseDTO<Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("Request body validation failed: {}", message);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.error("Validation failed: " + message));
    }

    /**
     * A catch-all handler for any other unhandled exceptions.
     * This prevents stack traces from being exposed to the client and provides a generic
     * "Internal Server Error" response, while logging the full exception for debugging.
     *
     * @param ex The caught Exception instance.
     * @return A ResponseEntity with a 500 Internal Server Error status.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiResponseDTO<Object>> handleAllUncaughtException(Exception ex) {
        log.error("An unexpected error occurred", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("An unexpected internal server error occurred. Please try again later."));
    }
}

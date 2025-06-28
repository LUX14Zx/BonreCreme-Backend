package com.tlfdt.bonrecreme.exception.resource;

import com.tlfdt.bonrecreme.exception.custom.CustomExceptionHandler;

/**
 * A specific exception for cases where a resource (e.g., from the database) is not found.
 * This extends the base custom exception and allows for more specific handling,
 * typically resulting in a 404 Not Found HTTP status.
 */
public class ResourceNotFoundException extends CustomExceptionHandler {
    public ResourceNotFoundException(String message)
    {
        super(message);
    }
}

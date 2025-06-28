package com.tlfdt.bonrecreme.service.table;

import com.tlfdt.bonrecreme.controller.api.v1.manager.dto.table.TableRequestDTO;
import com.tlfdt.bonrecreme.controller.api.v1.manager.dto.table.TableResponseDTO;
import com.tlfdt.bonrecreme.exception.custom.CustomExceptionHandler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * A service interface for managing the business logic of restaurant tables.
 * <p>
 * This service provides a comprehensive API for creating, retrieving, updating,
 * and deleting seating tables, including support for pagination to handle
 * large numbers of tables efficiently.
 */
public interface SeatTableService {

    /**
     * Creates a new restaurant table based on the provided data.
     * The new table will be set to an 'AVAILABLE' status by default.
     *
     * @param requestDTO A DTO containing the details for the new table.
     * @return A {@link TableResponseDTO} representing the newly created table.
     * @throws CustomExceptionHandler if a table with the same number already exists.
     */
    TableResponseDTO createTable(TableRequestDTO requestDTO);

    /**
     * Retrieves a specific table by its unique identifier.
     *
     * @param id The unique ID of the table to retrieve.
     * @return A {@link TableResponseDTO} representing the found table.
     * @throws CustomExceptionHandler if no table with the given ID is found.
     */
    TableResponseDTO getTableById(Long id);

    /**
     * Retrieves a paginated list of all restaurant tables.
     * <p>
     * Using pagination is crucial for performance and scalability, preventing the system
     * from loading all tables into memory at once.
     *
     * @return A {@link List} of {@link TableResponseDTO}s.
     */
    List<TableResponseDTO> getAllTables();

    /**
     * Updates an existing table with new data.
     *
     * @param id         The unique ID of the table to update.
     * @param requestDTO A DTO containing the updated details.
     * @return A {@link TableResponseDTO} representing the updated table.
     * @throws CustomExceptionHandler if the table to update is not found.
     */
    TableResponseDTO updateTable(Long id, TableRequestDTO requestDTO);

    /**
     * Deletes a table by its unique identifier.
     *
     * @param id The unique ID of the table to delete.
     * @throws CustomExceptionHandler if the table to delete is not found.
     */
    void deleteTable(Long id);
}

package com.tlfdt.bonrecreme.controller.api.v1.manager;

import com.tlfdt.bonrecreme.controller.api.v1.dto.ApiResponseDTO;
import com.tlfdt.bonrecreme.controller.api.v1.manager.dto.table.TableRequestDTO;
import com.tlfdt.bonrecreme.controller.api.v1.manager.dto.table.TableResponseDTO;
import com.tlfdt.bonrecreme.service.table.SeatTableService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing restaurant tables.
 * Provides CRUD endpoints for creating, retrieving, updating, and deleting tables.
 * All inputs are validated to ensure data integrity.
 */
@RestController
@RequestMapping("/api/v1/manager/tables")
@RequiredArgsConstructor
@Validated // Enables validation for path variables and request parameters.
public class TableController {

    private final SeatTableService tableService;

    /**
     * Creates a new restaurant table.
     *
     * @param requestDTO The DTO containing the details of the table to create. Must be valid.
     * @return A standardized API response containing the details of the created table.
     */
    @PostMapping
    public ResponseEntity<ApiResponseDTO<TableResponseDTO>> createTable(@Valid @RequestBody TableRequestDTO requestDTO) {
        TableResponseDTO createdTable = tableService.createTable(requestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(createdTable, "table created successfully."));
    }

    /**
     * Retrieves a specific table by its ID.
     *
     * @param id The unique identifier of the table. Must be a positive number.
     * @return A standardized API response containing the details of the fetched table.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<TableResponseDTO>> getTableById(
            @PathVariable @Positive(message = "table ID must be a positive number.") Long id) {
        TableResponseDTO table = tableService.getTableById(id);
        return ResponseEntity.ok(ApiResponseDTO.success(table, "table fetched successfully."));
    }

    /**
     * Retrieves a list of all restaurant tables.
     *
     * @return A standardized API response containing a list of all tables.
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<TableResponseDTO>>> getAllTables() {
        List<TableResponseDTO> tablesList = tableService.getAllTables();
        return ResponseEntity.ok(ApiResponseDTO.success(tablesList, "All tables fetched successfully."));
    }

    /**
     * Updates an existing table.
     *
     * @param id         The unique identifier of the table to update. Must be a positive number.
     * @param requestDTO The DTO containing the updated table details. Must be valid.
     * @return A standardized API response containing the details of the updated table.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<TableResponseDTO>> updateTable(
            @PathVariable @Positive(message = "table ID must be a positive number.") Long id,
            @Valid @RequestBody TableRequestDTO requestDTO) {
        TableResponseDTO updatedTable = tableService.updateTable(id, requestDTO);
        return ResponseEntity.ok(ApiResponseDTO.success(updatedTable, "table updated successfully."));
    }

    /**
     * Deletes a table by its ID.
     *
     * @param id The unique identifier of the table to delete. Must be a positive number.
     * @return A standardized API response confirming the deletion.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteTable(
            @PathVariable @Positive(message = "table ID must be a positive number.") Long id) {
        tableService.deleteTable(id);
        return ResponseEntity.ok(ApiResponseDTO.success("table deleted successfully."));
    }
}

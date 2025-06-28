package com.tlfdt.bonrecreme.controller.api.v1.customer;

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
@RequestMapping("/api/v1/customer/get_tables")
@RequiredArgsConstructor
@Validated // Enables validation for path variables and request parameters.
public class CustomerTableController {

    private final SeatTableService tableService;


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
}

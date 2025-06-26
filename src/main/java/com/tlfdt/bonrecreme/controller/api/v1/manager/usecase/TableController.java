package com.tlfdt.bonrecreme.controller.api.v1.manager.usecase;

import com.tlfdt.bonrecreme.controller.api.v1.dto.ApiResponseDTO;
import com.tlfdt.bonrecreme.controller.api.v1.manager.dto.Table.TableRequestDTO;
import com.tlfdt.bonrecreme.controller.api.v1.manager.dto.Table.TableResponseDTO;
import com.tlfdt.bonrecreme.service.table.SeatTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/manager/tables")
@RequiredArgsConstructor
public class TableController {

    private final SeatTableService tableService;

    @PostMapping
    public ResponseEntity<ApiResponseDTO<TableResponseDTO>> createTable(@RequestBody TableRequestDTO requestDTO) {
        TableResponseDTO createdTable = tableService.createTable(requestDTO);
        ApiResponseDTO<TableResponseDTO> response = ApiResponseDTO.<TableResponseDTO>builder()
                .api_data(createdTable)
                .status("success")
                .message("Table created successfully")
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<TableResponseDTO>> getTableById(@PathVariable Long id) {
        TableResponseDTO table = tableService.getTableById(id);
        ApiResponseDTO<TableResponseDTO> response = ApiResponseDTO.<TableResponseDTO>builder()
                .api_data(table)
                .status("success")
                .message("Table fetched successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<TableResponseDTO>>> getAllTables() {
        List<TableResponseDTO> tables = tableService.getAllTables();
        ApiResponseDTO<List<TableResponseDTO>> response = ApiResponseDTO.<List<TableResponseDTO>>builder()
                .api_data(tables)
                .status("success")
                .message("All tables fetched successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<TableResponseDTO>> updateTable(@PathVariable Long id, @RequestBody TableRequestDTO requestDTO) {
        TableResponseDTO updatedTable = tableService.updateTable(id, requestDTO);
        ApiResponseDTO<TableResponseDTO> response = ApiResponseDTO.<TableResponseDTO>builder()
                .api_data(updatedTable)
                .status("success")
                .message("Table updated successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteTable(@PathVariable Long id) {
        tableService.deleteTable(id);
        ApiResponseDTO<Void> response = ApiResponseDTO.<Void>builder()
                .status("success")
                .message("Table deleted successfully")
                .build();
        return ResponseEntity.ok(response);
    }
}
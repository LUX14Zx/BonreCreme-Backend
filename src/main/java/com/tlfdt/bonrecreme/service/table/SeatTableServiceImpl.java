package com.tlfdt.bonrecreme.service.table;

import com.tlfdt.bonrecreme.controller.api.v1.manager.dto.Table.TableRequestDTO;
import com.tlfdt.bonrecreme.controller.api.v1.manager.dto.Table.TableResponseDTO;
import com.tlfdt.bonrecreme.exception.custom.CustomExceptionHandler;
import com.tlfdt.bonrecreme.model.restaurant.SeatTable;
import com.tlfdt.bonrecreme.model.restaurant.enums.TableStatus;
import com.tlfdt.bonrecreme.repository.restaurant.SeatTableRepository;
import com.tlfdt.bonrecreme.utils.table.mapper.SeatTableMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for managing restaurant tables.
 * Implements the business logic for CRUD operations on SeatTable entities.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SeatTableServiceImpl implements SeatTableService {

    private final SeatTableRepository tableRepository;
    private final SeatTableMapper tableMapper;

    @Override
    @Transactional("restaurantTransactionManager")
    public TableResponseDTO createTable(TableRequestDTO requestDTO) {
        // Business logic validation: Prevent creating tables with a duplicate number.
        if (tableRepository.findByTableNumber(requestDTO.getTableNumber()).isPresent()) {
            throw new CustomExceptionHandler("A table with number " + requestDTO.getTableNumber() + " already exists.");
        }

        SeatTable table = tableMapper.toNewEntity(requestDTO);

        SeatTable savedTable = tableRepository.save(table);
        log.info("Created new table with ID: {} and number: {}", savedTable.getId(), savedTable.getTableNumber());
        return tableMapper.toResponseDTO(savedTable);
    }

    @Override
    @Transactional(value = "restaurantTransactionManager", readOnly = true)
    public TableResponseDTO getTableById(Long id) {
        SeatTable table = findTableById(id);
        return tableMapper.toResponseDTO(table);
    }

    @Override
    @Transactional(value = "restaurantTransactionManager", readOnly = true)
    public Page<TableResponseDTO> getAllTables(Pageable pageable) {
        log.info("Fetching all tables for page request: {}", pageable);
        Page<SeatTable> tablesPage = tableRepository.findAll(pageable);
        // Use the mapper to convert the Page of entities to a Page of DTOs
        return tablesPage.map(tableMapper::toResponseDTO);
    }

    @Override
    @Transactional("restaurantTransactionManager")
    public TableResponseDTO updateTable(Long id, TableRequestDTO requestDTO) {
        SeatTable tableToUpdate = findTableById(id);

        // Business logic validation: Ensure the new table number isn't already taken by another table.
        tableRepository.findByTableNumber(requestDTO.getTableNumber())
                .ifPresent(existingTable -> {
                    if (!existingTable.getId().equals(id)) {
                        throw new CustomExceptionHandler("Cannot update to table number " + requestDTO.getTableNumber() + " as it is already in use by another table.");
                    }
                });

        tableMapper.updateEntityFromDTO(tableToUpdate, requestDTO);

        SeatTable updatedTable = tableRepository.save(tableToUpdate);
        log.info("Updated table with ID: {}", updatedTable.getId());
        return tableMapper.toResponseDTO(updatedTable);
    }

    @Override
    @Transactional("restaurantTransactionManager")
    public void deleteTable(Long id) {
        // Use a more robust check that also ensures the entity exists before trying to delete.
        SeatTable tableToDelete = findTableById(id);

        // Add business logic to prevent deletion under certain conditions.
        if (tableToDelete.getStatus() == TableStatus.OCCUPIED) {
             throw new CustomExceptionHandler("Cannot delete a table that is currently occupied.");
        }

        tableRepository.delete(tableToDelete);
        log.info("Deleted table with ID: {}", id);
    }

    /**
     * Private helper to find a SeatTable by its ID, providing a consistent exception message.
     * This avoids code duplication and centralizes the "not found" logic.
     *
     * @param id The ID of the table to find.
     * @return The found SeatTable entity.
     * @throws CustomExceptionHandler if the table is not found.
     */
    private SeatTable findTableById(Long id) {
        return tableRepository.findById(id)
                .orElseThrow(() -> new CustomExceptionHandler("Table not found with ID: " + id));
    }
}

// src/main/java/com/tlfdt/bonrecreme/service/table/SeatTableServiceImpl.java

package com.tlfdt.bonrecreme.service.table;

import com.tlfdt.bonrecreme.controller.api.v1.manager.dto.table.TableRequestDTO;
import com.tlfdt.bonrecreme.controller.api.v1.manager.dto.table.TableResponseDTO;
import com.tlfdt.bonrecreme.exception.custom.CustomExceptionHandler;
import com.tlfdt.bonrecreme.model.restaurant.SeatTable;
import com.tlfdt.bonrecreme.model.restaurant.enums.TableStatus;
import com.tlfdt.bonrecreme.repository.restaurant.BillRepository;
import com.tlfdt.bonrecreme.repository.restaurant.SeatTableRepository;
import com.tlfdt.bonrecreme.utils.table.mapper.SeatTableMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class SeatTableServiceImpl implements SeatTableService {

    private final SeatTableRepository tableRepository;
    private final BillRepository billRepository;
    private final SeatTableMapper tableMapper;

    @Override
    @Transactional("restaurantTransactionManager")
    public TableResponseDTO createTable(TableRequestDTO requestDTO) {
        SeatTable table = tableMapper.toNewEntity(requestDTO);

        SeatTable savedTable = tableRepository.save(table);
        log.info("Created new table with ID: {}", savedTable.getId());
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
    public List<TableResponseDTO> getAllTables() {
        log.info("Fetching all tables");
        List<SeatTable> tables = tableRepository.findAll();
        return tables.stream()
                .map(tableMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional("restaurantTransactionManager")
    public TableResponseDTO updateTable(Long id, TableRequestDTO requestDTO) {
        SeatTable tableToUpdate = findTableById(id);

        tableMapper.updateEntityFromDTO(tableToUpdate, requestDTO);

        SeatTable updatedTable = tableRepository.save(tableToUpdate);
        log.info("Updated table with ID: {}", updatedTable.getId());
        return tableMapper.toResponseDTO(updatedTable);
    }

    @Override
    @Transactional("restaurantTransactionManager")
    public void deleteTable(Long id) {
        SeatTable tableToDelete = findTableById(id);

        if (tableToDelete.getStatus() == TableStatus.OCCUPIED) {
            throw new CustomExceptionHandler("Cannot delete a table that is currently occupied.");
        }

        // Use the new repository method for a precise check
        if (billRepository.existsBySeatTableId(id)) {
            throw new CustomExceptionHandler("Cannot delete table with ID: " + id + " because it has associated bills.");
        }

        tableRepository.delete(tableToDelete);
        log.info("Deleted table with ID: {}", id);
    }

    private SeatTable findTableById(Long id) {
        return tableRepository.findById(id)
                .orElseThrow(() -> new CustomExceptionHandler("table not found with ID: " + id));
    }
}
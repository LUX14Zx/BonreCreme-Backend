package com.tlfdt.bonrecreme.service.table;

import com.tlfdt.bonrecreme.controller.api.v1.manager.dto.Table.TableRequestDTO;
import com.tlfdt.bonrecreme.controller.api.v1.manager.dto.Table.TableResponseDTO;
import com.tlfdt.bonrecreme.exception.custom.CustomExceptionHandler;
import com.tlfdt.bonrecreme.model.restaurant.SeatTable;
import com.tlfdt.bonrecreme.model.restaurant.enums.TableStatus;
import com.tlfdt.bonrecreme.repository.restaurant.SeatTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatTableServiceImpl implements SeatTableService {

    private final SeatTableRepository tableRepository;

    @Override
    @Transactional("restaurantTransactionManager")
    public TableResponseDTO createTable(TableRequestDTO requestDTO) {
        SeatTable table = new SeatTable();
        table.setTableNumber(requestDTO.getTableNumber());
        table.setSeatingCapacity(requestDTO.getSeatingCapacity());
        table.setStatus(requestDTO.getStatus() != null ? requestDTO.getStatus() : TableStatus.Available);

        SeatTable savedTable = tableRepository.save(table);
        return TableResponseDTO.fromRestaurantTable(savedTable);
    }

    @Override
    public TableResponseDTO getTableById(Long id) {
        SeatTable table = tableRepository.findById(id)
                .orElseThrow(() -> new CustomExceptionHandler("Table not found"));
        return TableResponseDTO.fromRestaurantTable(table);
    }

    @Override
    public List<TableResponseDTO> getAllTables() {
        return tableRepository.findAll().stream()
                .map(TableResponseDTO::fromRestaurantTable)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional("restaurantTransactionManager")
    public TableResponseDTO updateTable(Long id, TableRequestDTO requestDTO) {
        SeatTable table = tableRepository.findById(id)
                .orElseThrow(() -> new CustomExceptionHandler("Table not found"));

        table.setTableNumber(requestDTO.getTableNumber());
        table.setSeatingCapacity(requestDTO.getSeatingCapacity());
        table.setStatus(requestDTO.getStatus());

        SeatTable updatedTable = tableRepository.save(table);
        return TableResponseDTO.fromRestaurantTable(updatedTable);
    }

    @Override
    @Transactional("restaurantTransactionManager")
    public void deleteTable(Long id) {
        if (!tableRepository.existsById(id)) {
            throw new CustomExceptionHandler("Table not found");
        }
        tableRepository.deleteById(id);
    }
}
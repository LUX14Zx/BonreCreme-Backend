package com.tlfdt.bonrecreme.service.table;

import com.tlfdt.bonrecreme.controller.api.v1.manager.dto.Table.TableRequestDTO;
import com.tlfdt.bonrecreme.controller.api.v1.manager.dto.Table.TableResponseDTO;


import java.util.List;

public interface SeatTableService {
    TableResponseDTO createTable(TableRequestDTO requestDTO);
    TableResponseDTO getTableById(Long id);
    List<TableResponseDTO> getAllTables();
    TableResponseDTO updateTable(Long id, TableRequestDTO requestDTO);
    void deleteTable(Long id);
}
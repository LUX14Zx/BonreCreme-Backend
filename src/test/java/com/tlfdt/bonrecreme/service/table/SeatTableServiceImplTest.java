package com.tlfdt.bonrecreme.service.table;

import com.tlfdt.bonrecreme.controller.api.v1.manager.dto.table.TableRequestDTO;
import com.tlfdt.bonrecreme.controller.api.v1.manager.dto.table.TableResponseDTO;
import com.tlfdt.bonrecreme.exception.custom.CustomExceptionHandler;
import com.tlfdt.bonrecreme.model.restaurant.MenuItem;
import com.tlfdt.bonrecreme.model.restaurant.SeatTable;
import com.tlfdt.bonrecreme.model.restaurant.enums.TableStatus;
import com.tlfdt.bonrecreme.repository.restaurant.SeatTableRepository;
import com.tlfdt.bonrecreme.utils.table.mapper.SeatTableMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeatTableServiceImplTest {

    @Mock
    private SeatTableRepository tableRepository;

    @Mock
    private SeatTableMapper tableMapper;

    @InjectMocks
    private SeatTableServiceImpl seatTableService;

    private TableRequestDTO tableRequestDTO;
    private SeatTable seatTable;
    private TableResponseDTO tableResponseDTO;

    @BeforeEach
    void setUp() {
        tableRequestDTO = new TableRequestDTO(4, TableStatus.AVAILABLE);


        seatTable = SeatTable.builder()
                .id(1L)
                .seatingCapacity(4)
                .status(TableStatus.AVAILABLE)
                .build();

        tableResponseDTO = new TableResponseDTO(seatTable);
    }

    @Test
    void testCreateTable_Success() {
        when(tableMapper.toNewEntity(any(TableRequestDTO.class))).thenReturn(seatTable);
        when(tableRepository.save(any(SeatTable.class))).thenReturn(seatTable);
        when(tableMapper.toResponseDTO(any(SeatTable.class))).thenReturn(tableResponseDTO);

        TableResponseDTO result = seatTableService.createTable(tableRequestDTO);

        assertNotNull(result);
        assertEquals(tableResponseDTO.getId(), result.getId());
        verify(tableMapper, times(1)).toNewEntity(any(TableRequestDTO.class));
        verify(tableRepository, times(1)).save(any(SeatTable.class));
        verify(tableMapper, times(1)).toResponseDTO(any(SeatTable.class));
    }

    @Test
    void testGetTableById_Success() {
        when(tableRepository.findById(anyLong())).thenReturn(Optional.of(seatTable));
        when(tableMapper.toResponseDTO(any(SeatTable.class))).thenReturn(tableResponseDTO);

        TableResponseDTO result = seatTableService.getTableById(1L);

        assertNotNull(result);
        assertEquals(tableResponseDTO.getId(), result.getId());
        verify(tableRepository, times(1)).findById(anyLong());
        verify(tableMapper, times(1)).toResponseDTO(any(SeatTable.class));
    }

    @Test
    void testGetTableById_NotFound() {
        when(tableRepository.findById(anyLong())).thenReturn(Optional.empty());

        CustomExceptionHandler exception = assertThrows(CustomExceptionHandler.class, () -> seatTableService.getTableById(1L));

        assertEquals("table not found with ID: 1", exception.getMessage());
        verify(tableRepository, times(1)).findById(anyLong());
        verifyNoInteractions(tableMapper);
    }

    @Test
    void testGetAllTables_Success() {
        when(tableRepository.findAll()).thenReturn(Arrays.asList(seatTable));
        when(tableMapper.toResponseDTO(any(SeatTable.class))).thenReturn(tableResponseDTO);

        List<TableResponseDTO> result = seatTableService.getAllTables();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(tableResponseDTO.getId(), result.get(0).getId());
        verify(tableRepository, times(1)).findAll();
        verify(tableMapper, times(1)).toResponseDTO(any(SeatTable.class));
    }

    @Test
    void testUpdateTable_Success() {
        when(tableRepository.findById(anyLong())).thenReturn(Optional.of(seatTable));
        when(tableRepository.save(any(SeatTable.class))).thenReturn(seatTable);
        when(tableMapper.toResponseDTO(any(SeatTable.class))).thenReturn(tableResponseDTO);

        TableResponseDTO result = seatTableService.updateTable(1L, tableRequestDTO);

        assertNotNull(result);
        assertEquals(tableResponseDTO.getId(), result.getId());
        verify(tableRepository, times(1)).findById(anyLong());
        verify(tableMapper, times(1)).updateEntityFromDTO(any(SeatTable.class), any(TableRequestDTO.class));
        verify(tableRepository, times(1)).save(any(SeatTable.class));
        verify(tableMapper, times(1)).toResponseDTO(any(SeatTable.class));
    }

    @Test
    void testUpdateTable_NotFound() {
        when(tableRepository.findById(anyLong())).thenReturn(Optional.empty());

        CustomExceptionHandler exception = assertThrows(CustomExceptionHandler.class, () -> seatTableService.updateTable(1L, tableRequestDTO));

        assertEquals("table not found with ID: 1", exception.getMessage());
        verify(tableRepository, times(1)).findById(anyLong());
        verifyNoInteractions(tableMapper);
        verify(tableRepository, never()).save(any(SeatTable.class));
    }

    @Test
    void testDeleteTable_Success() {
        seatTable.setStatus(TableStatus.AVAILABLE);
        when(tableRepository.findById(anyLong())).thenReturn(Optional.of(seatTable));
        doNothing().when(tableRepository).delete(any(SeatTable.class));

        seatTableService.deleteTable(1L);

        verify(tableRepository, times(1)).findById(anyLong());
        verify(tableRepository, times(1)).delete(any(SeatTable.class));
    }

    @Test
    void testDeleteTable_NotFound() {
        when(tableRepository.findById(anyLong())).thenReturn(Optional.empty());

        CustomExceptionHandler exception = assertThrows(CustomExceptionHandler.class, () -> seatTableService.deleteTable(1L));

        assertEquals("table not found with ID: 1", exception.getMessage());
        verify(tableRepository, times(1)).findById(anyLong());
        verify(tableRepository, never()).delete(any(SeatTable.class));
    }

    @Test
    void testDeleteTable_Occupied() {
        seatTable.setStatus(TableStatus.OCCUPIED);
        when(tableRepository.findById(anyLong())).thenReturn(Optional.of(seatTable));

        CustomExceptionHandler exception = assertThrows(CustomExceptionHandler.class, () -> seatTableService.deleteTable(1L));

        assertEquals("Cannot delete a table that is currently occupied.", exception.getMessage());
        verify(tableRepository, times(1)).findById(anyLong());
        verify(tableRepository, never()).delete(any(SeatTable.class));
    }
}
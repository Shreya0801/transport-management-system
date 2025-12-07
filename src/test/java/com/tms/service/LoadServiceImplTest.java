package com.tms.service;

import com.tms.dto.LoadDto;
import com.tms.entity.Load;
import com.tms.exception.ResourceNotFoundException;
import com.tms.repository.LoadRepository;
import com.tms.service.impl.LoadServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoadServiceImplTest {
    @Mock
    private LoadRepository loadRepository;
    @InjectMocks
    private LoadServiceImpl loadService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateLoad() {
        LoadDto dto = new LoadDto();
        dto.setShipperId("shipper1");
        dto.setLoadingCity("A");
        dto.setUnloadingCity("B");
        dto.setLoadingDate(Instant.now());
        dto.setProductType("General");
        dto.setWeight(1000);
        dto.setWeightUnit("KG");
        dto.setTruckType("T1");
        dto.setNoOfTrucks(2);

        Load saved = new Load();
        saved.setLoadId(UUID.randomUUID());
        saved.setShipperId(dto.getShipperId());
        saved.setLoadingCity(dto.getLoadingCity());
        saved.setUnloadingCity(dto.getUnloadingCity());
        saved.setLoadingDate(dto.getLoadingDate());
        saved.setProductType(dto.getProductType());
        saved.setWeight(dto.getWeight());
        saved.setWeightUnit(dto.getWeightUnit());
        saved.setTruckType(dto.getTruckType());
        saved.setNoOfTrucks(dto.getNoOfTrucks());
        saved.setStatus("POSTED");
        saved.setDatePosted(Instant.now());

        when(loadRepository.save(any(Load.class))).thenReturn(saved);
        LoadDto result = loadService.createLoad(dto);
        assertEquals(dto.getShipperId(), result.getShipperId());
        assertEquals("POSTED", result.getStatus());
    }

    @Test
    void testGetLoadNotFound() {
        UUID id = UUID.randomUUID();
        when(loadRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> loadService.getLoad(id));
    }

    @Test
    void testCancelLoadBooked() {
        UUID id = UUID.randomUUID();
        Load load = new Load();
        load.setStatus("BOOKED");
        when(loadRepository.findById(id)).thenReturn(Optional.of(load));
        assertThrows(com.tms.exception.InvalidStatusTransitionException.class, () -> loadService.cancelLoad(id));
    }

    @Test
    void testCancelLoadSuccess() {
        UUID id = UUID.randomUUID();
        Load load = new Load();
        load.setStatus("POSTED");
        when(loadRepository.findById(id)).thenReturn(Optional.of(load));
        when(loadRepository.save(any(Load.class))).thenReturn(load);
        LoadDto result = loadService.cancelLoad(id);
        assertEquals("CANCELLED", result.getStatus());
    }

}

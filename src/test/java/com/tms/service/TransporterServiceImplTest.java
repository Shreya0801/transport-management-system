package com.tms.service;

import com.tms.dto.TransporterDto;
import com.tms.entity.Transporter;
import com.tms.repository.TransporterRepository;
import com.tms.service.impl.TransporterServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransporterServiceImplTest {
    @Mock
    private TransporterRepository transporterRepository;
    @InjectMocks
    private TransporterServiceImpl transporterService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterTransporter() {
        TransporterDto dto = new TransporterDto();
        dto.setCompanyName("Acme");
        dto.setRating(4.5);
        Transporter saved = new Transporter();
        saved.setTransporterId(UUID.randomUUID());
        saved.setCompanyName(dto.getCompanyName());
        saved.setRating(dto.getRating());
        when(transporterRepository.save(any(Transporter.class))).thenReturn(saved);
        TransporterDto result = transporterService.register(dto);
        assertEquals(dto.getCompanyName(), result.getCompanyName());
        assertEquals(dto.getRating(), result.getRating());
    }

    @Test
    void testGetTransporterNotFound() {
        UUID id = UUID.randomUUID();
        when(transporterRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> transporterService.getTransporter(id));
    }
}


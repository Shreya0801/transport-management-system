package com.tms.service;

import com.tms.dto.BidDto;
import com.tms.entity.Bid;
import com.tms.entity.Load;
import com.tms.entity.Transporter;
import com.tms.exception.InsufficientCapacityException;
import com.tms.exception.InvalidStatusTransitionException;
import com.tms.exception.ResourceNotFoundException;
import com.tms.repository.BidRepository;
import com.tms.repository.LoadRepository;
import com.tms.repository.TransporterRepository;
import com.tms.service.impl.BidServiceImpl;
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

class BidServiceImplTest {
    @Mock
    private BidRepository bidRepository;
    @Mock
    private LoadRepository loadRepository;
    @Mock
    private TransporterRepository transporterRepository;
    @InjectMocks
    private BidServiceImpl bidService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSubmitBidOnCancelledLoad() {
        BidDto dto = new BidDto();
        UUID loadId = UUID.randomUUID();
        dto.setLoadId(loadId);
        Load load = new Load();
        load.setStatus("CANCELLED");
        when(loadRepository.findById(loadId)).thenReturn(Optional.of(load));
        assertThrows(InvalidStatusTransitionException.class, () -> bidService.submitBid(dto));
    }

    @Test
    void testSubmitBidInsufficientCapacity() {
        BidDto dto = new BidDto();
        UUID loadId = UUID.randomUUID();
        UUID transporterId = UUID.randomUUID();
        dto.setLoadId(loadId);
        dto.setTransporterId(transporterId);
        dto.setTrucksOffered(5);
        Load load = new Load();
        load.setStatus("POSTED");
        load.setTruckType("T1");
        when(loadRepository.findById(loadId)).thenReturn(Optional.of(load));
        Transporter t = new Transporter();
        Transporter.TruckCapacity tc = new Transporter.TruckCapacity();
        tc.setTruckType("T1");
        tc.setCount(2);
        t.setAvailableTrucks(java.util.List.of(tc));
        when(transporterRepository.findById(transporterId)).thenReturn(Optional.of(t));
        assertThrows(InsufficientCapacityException.class, () -> bidService.submitBid(dto));
    }

    @Test
    void testSubmitBidLoadNotFound() {
        BidDto dto = new BidDto();
        UUID loadId = UUID.randomUUID();
        dto.setLoadId(loadId);
        when(loadRepository.findById(loadId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> bidService.submitBid(dto));
    }
}


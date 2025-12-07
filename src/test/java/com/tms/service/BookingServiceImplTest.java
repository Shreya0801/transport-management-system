package com.tms.service;

import com.tms.dto.BookingDto;
import com.tms.entity.Bid;
import com.tms.entity.Booking;
import com.tms.entity.Load;
import com.tms.entity.Transporter;
import com.tms.exception.LoadAlreadyBookedException;
import com.tms.exception.ResourceNotFoundException;
import com.tms.repository.BidRepository;
import com.tms.repository.BookingRepository;
import com.tms.repository.LoadRepository;
import com.tms.repository.TransporterRepository;
import com.tms.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {
    @Mock
    private BidRepository bidRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private LoadRepository loadRepository;
    @Mock
    private TransporterRepository transporterRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAcceptBidAlreadyBooked() {
        UUID bidId = UUID.randomUUID();
        Bid bid = mock(Bid.class);
        Load load = mock(Load.class);
        when(bidRepository.findById(bidId)).thenReturn(Optional.of(bid));
        when(bid.getLoad()).thenReturn(load);
        when(load.getLoadId()).thenReturn(UUID.randomUUID());
        when(loadRepository.findById(any())).thenReturn(Optional.of(load));
        when(bidRepository.findByLoadAndStatus(load, "ACCEPTED")).thenReturn(Collections.singletonList(mock(Bid.class)));
        assertThrows(LoadAlreadyBookedException.class, () -> bookingService.acceptBid(bidId, 1));
    }

    @Test
    void testAcceptBidNotFound() {
        UUID bidId = UUID.randomUUID();
        when(bidRepository.findById(bidId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> bookingService.acceptBid(bidId, 1));
    }

    @Test
    void testCancelBookingAlreadyCancelled() {
        UUID bookingId = UUID.randomUUID();
        Booking b = new Booking();
        b.setStatus("CANCELLED");
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(b));
        BookingDto result = bookingService.cancelBooking(bookingId);
        assertEquals("CANCELLED", result.getStatus());
    }

    @Test
    void testCancelBookingRestoresTrucksAndUpdatesStatus() {
        UUID bookingId = UUID.randomUUID();
        Booking b = new Booking();
        b.setStatus("CONFIRMED");
        Transporter t = new Transporter();
        Transporter.TruckCapacity tc = new Transporter.TruckCapacity();
        tc.setTruckType("T1");
        tc.setCount(1);
        t.setAvailableTrucks(java.util.List.of(tc));
        b.setTransporter(t);
        Load load = new Load();
        load.setTruckType("T1");
        load.setNoOfTrucks(2);
        b.setLoad(load);
        b.setAllocatedTrucks(1);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(b));
        when(bookingRepository.findByLoad(load)).thenReturn(java.util.List.of(b));
        when(transporterRepository.save(any())).thenReturn(t);
        when(loadRepository.save(any())).thenReturn(load);
        when(bookingRepository.save(any())).thenReturn(b);
        BookingDto result = bookingService.cancelBooking(bookingId);
        assertEquals("CANCELLED", result.getStatus());
    }

    @Test
    void testGetBookingNotFound() {
        UUID bookingId = UUID.randomUUID();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> bookingService.getBooking(bookingId));
    }
}

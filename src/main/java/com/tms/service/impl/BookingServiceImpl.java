package com.tms.service.impl;

import com.tms.dto.BookingDto;
import com.tms.entity.Bid;
import com.tms.entity.Booking;
import com.tms.entity.Load;
import com.tms.entity.Transporter;
import com.tms.exception.InsufficientCapacityException;
import com.tms.exception.LoadAlreadyBookedException;
import com.tms.exception.ResourceNotFoundException;
import com.tms.repository.BidRepository;
import com.tms.repository.BookingRepository;
import com.tms.repository.LoadRepository;
import com.tms.repository.TransporterRepository;
import com.tms.service.BookingService;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class BookingServiceImpl implements BookingService {

    private final BidRepository bidRepository;
    private final BookingRepository bookingRepository;
    private final LoadRepository loadRepository;
    private final TransporterRepository transporterRepository;

    public BookingServiceImpl(BidRepository bidRepository, BookingRepository bookingRepository, LoadRepository loadRepository, TransporterRepository transporterRepository) {
        this.bidRepository = bidRepository;
        this.bookingRepository = bookingRepository;
        this.loadRepository = loadRepository;
        this.transporterRepository = transporterRepository;
    }

    @Override
    @Transactional
    public BookingDto acceptBid(UUID bidId, int allocateTrucks) {
        try {
            Bid bid = bidRepository.findById(bidId).orElseThrow(() -> new ResourceNotFoundException("Bid not found"));
            // re-fetch load with latest version
            Load load = loadRepository.findById(bid.getLoad().getLoadId()).orElseThrow(() -> new ResourceNotFoundException("Load not found"));

            // ensure no other ACCEPTED bid exists
            List<Bid> accepted = bidRepository.findByLoadAndStatus(load, "ACCEPTED");
            if (!accepted.isEmpty()) {
                throw new LoadAlreadyBookedException("An accepted bid already exists for this load");
            }

            Transporter transporter = bid.getTransporter();

            // validate remaining trucks on load
            int allocatedSoFar = bookingRepository.findByLoad(load).stream().filter(bk -> !"CANCELLED".equals(bk.getStatus())).mapToInt(bk -> bk.getAllocatedTrucks()).sum();
            int remaining = load.getNoOfTrucks() - allocatedSoFar;
            if (remaining <= 0) {
                throw new LoadAlreadyBookedException("Load already fully booked");
            }
            if (allocateTrucks > remaining) {
                throw new InsufficientCapacityException("Trying to allocate more trucks than remaining on load");
            }

            // validate transporter capacity for truck type
            int available = transporter.getAvailableTrucks().stream()
                    .filter(tc -> tc.getTruckType().equals(load.getTruckType()))
                    .mapToInt(tc -> tc.getCount()).sum();
            if (allocateTrucks > available) {
                throw new InsufficientCapacityException("Transporter does not have enough trucks to allocate");
            }

            // deduct trucks from transporter
            transporter.getAvailableTrucks().stream()
                    .filter(tc -> tc.getTruckType().equals(load.getTruckType()))
                    .findFirst().ifPresent(tc -> tc.setCount(tc.getCount() - allocateTrucks));
            transporterRepository.save(transporter);

            // mark this bid ACCEPTED and others REJECTED
            bid.setStatus("ACCEPTED");
            bidRepository.save(bid);
            List<Bid> others = bidRepository.findByLoadAndStatus(load, "PENDING");
            for (Bid o : others) {
                if (!o.getBidId().equals(bid.getBidId())) {
                    o.setStatus("REJECTED");
                    bidRepository.save(o);
                }
            }

            // create booking
            Booking booking = new Booking();
            booking.setLoad(load);
            booking.setBid(bid);
            booking.setTransporter(transporter);
            booking.setAllocatedTrucks(allocateTrucks);
            booking.setFinalRate(bid.getProposedRate());
            booking.setStatus("CONFIRMED");
            booking.setBookedAt(Instant.now());

            Booking saved = bookingRepository.save(booking);

            // after saving booking, check if load is fully booked
            int newAllocated = allocatedSoFar + allocateTrucks;
            if (newAllocated >= load.getNoOfTrucks()) {
                load.setStatus("BOOKED");
            }
            loadRepository.save(load);
            return toDto(saved);
        } catch (ObjectOptimisticLockingFailureException | CannotAcquireLockException ex) {
            throw new LoadAlreadyBookedException("Conflict while booking the load (optimistic lock or deadlock)");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBooking(UUID bookingId) {
        Booking b = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        return toDto(b);
    }

    @Override
    @Transactional
    public BookingDto cancelBooking(UUID bookingId) {
        Booking b = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if ("CANCELLED".equals(b.getStatus())) return toDto(b);
        b.setStatus("CANCELLED");
        // restore trucks
        Transporter t = b.getTransporter();
        Load load = b.getLoad();
        t.getAvailableTrucks().stream().filter(tc -> tc.getTruckType().equals(load.getTruckType())).findFirst()
                .ifPresent(tc -> tc.setCount(tc.getCount() + b.getAllocatedTrucks()));
        transporterRepository.save(t);

        // update load status
        int allocatedSoFar = bookingRepository.findByLoad(load).stream().filter(x -> !"CANCELLED".equals(x.getStatus())).mapToInt(x -> x.getAllocatedTrucks()).sum();
        int remaining = load.getNoOfTrucks() - allocatedSoFar;
        if (remaining > 0) {
            load.setStatus("OPEN_FOR_BIDS");
        }
        loadRepository.save(load);

        bookingRepository.save(b);
        return toDto(b);
    }

    private BookingDto toDto(Booking b) {
        BookingDto dto = new BookingDto();
        dto.setBookingId(b.getBookingId());
        dto.setLoadId(b.getLoad() != null ? b.getLoad().getLoadId() : null);
        dto.setBidId(b.getBid() != null ? b.getBid().getBidId() : null);
        dto.setTransporterId(b.getTransporter() != null ? b.getTransporter().getTransporterId() : null);
        dto.setAllocatedTrucks(b.getAllocatedTrucks());
        dto.setFinalRate(b.getFinalRate());
        dto.setStatus(b.getStatus());
        dto.setBookedAt(b.getBookedAt());
        return dto;
    }
}

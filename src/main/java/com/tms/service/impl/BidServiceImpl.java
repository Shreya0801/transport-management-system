package com.tms.service.impl;

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
import com.tms.service.BidService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BidServiceImpl implements BidService {
    private final BidRepository bidRepository;
    private final LoadRepository loadRepository;
    private final TransporterRepository transporterRepository;

    public BidServiceImpl(BidRepository bidRepository, LoadRepository loadRepository, TransporterRepository transporterRepository) {
        this.bidRepository = bidRepository;
        this.loadRepository = loadRepository;
        this.transporterRepository = transporterRepository;
    }

    @Override
    @Transactional
    public BidDto submitBid(BidDto bidDto) {
        Load load = loadRepository.findById(bidDto.getLoadId()).orElseThrow(() -> new ResourceNotFoundException("Load not found"));
        if ("CANCELLED".equals(load.getStatus()) || "BOOKED".equals(load.getStatus())) {
            throw new InvalidStatusTransitionException("Cannot bid on CANCELLED or BOOKED loads");
        }
        Transporter t = transporterRepository.findById(bidDto.getTransporterId()).orElseThrow(() -> new ResourceNotFoundException("Transporter not found"));
        // validate capacity
        int available = t.getAvailableTrucks().stream()
                .filter(tc -> tc.getTruckType().equals(load.getTruckType()))
                .mapToInt(tc -> tc.getCount()).sum();
        if (bidDto.getTrucksOffered() > available) {
            throw new InsufficientCapacityException("Transporter does not have enough trucks of type " + load.getTruckType());
        }
        Bid b = new Bid();
        b.setLoad(load);
        b.setTransporter(t);
        b.setProposedRate(bidDto.getProposedRate());
        b.setTrucksOffered(bidDto.getTrucksOffered());
        b.setStatus("PENDING");
        b.setSubmittedAt(Instant.now());

        // update load status to OPEN_FOR_BIDS if first bid
        if (load.getBids() == null || load.getBids().isEmpty()) {
            load.setStatus("OPEN_FOR_BIDS");
            loadRepository.save(load);
        }

        Bid saved = bidRepository.save(b);
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BidDto getBid(UUID bidId) {
        Bid b = bidRepository.findById(bidId).orElseThrow(() -> new ResourceNotFoundException("Bid not found"));
        return toDto(b);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BidDto> findByFilter(UUID loadId, UUID transporterId, String status) {
        List<Bid> all = bidRepository.findAll();
        return all.stream()
                .filter(b -> (loadId == null || b.getLoad().getLoadId().equals(loadId))
                        && (transporterId == null || b.getTransporter().getTransporterId().equals(transporterId))
                        && (status == null || b.getStatus().equals(status)))
                .map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BidDto rejectBid(UUID bidId) {
        Bid b = bidRepository.findById(bidId).orElseThrow(() -> new ResourceNotFoundException("Bid not found"));
        b.setStatus("REJECTED");
        return toDto(bidRepository.save(b));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BidDto> getBestBids(UUID loadId) {
        Load load = loadRepository.findById(loadId).orElseThrow(() -> new ResourceNotFoundException("Load not found"));
        List<Bid> bids = load.getBids() != null ? new ArrayList<>(load.getBids()) : new ArrayList<>();
        // compute score = (1 / proposedRate) * 0.7 + (rating / 5) * 0.3
        List<BidDto> dtos = bids.stream().map(this::toDto).collect(Collectors.toList());
        dtos.sort(Comparator.comparingDouble(b -> -((1.0 / b.getProposedRate()) * 0.7 + (getRating(b.getTransporterId()) / 5.0) * 0.3)));
        return dtos;
    }

    private double getRating(UUID transporterId) {
        return transporterRepository.findById(transporterId).map(Transporter::getRating).orElse(0.0);
    }

    private BidDto toDto(Bid b) {
        BidDto dto = new BidDto();
        dto.setBidId(b.getBidId());
        dto.setLoadId(b.getLoad() != null ? b.getLoad().getLoadId() : null);
        dto.setTransporterId(b.getTransporter() != null ? b.getTransporter().getTransporterId() : null);
        dto.setProposedRate(b.getProposedRate());
        dto.setTrucksOffered(b.getTrucksOffered());
        dto.setStatus(b.getStatus());
        dto.setSubmittedAt(b.getSubmittedAt());
        return dto;
    }
}


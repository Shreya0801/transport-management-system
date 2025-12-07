package com.tms.service.impl;

import com.tms.dto.TransporterDto;
import com.tms.entity.Transporter;
import com.tms.exception.ResourceNotFoundException;
import com.tms.repository.TransporterRepository;
import com.tms.service.TransporterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TransporterServiceImpl implements TransporterService {

    private final TransporterRepository transporterRepository;

    public TransporterServiceImpl(TransporterRepository transporterRepository) {
        this.transporterRepository = transporterRepository;
    }

    @Override
    @Transactional
    public TransporterDto register(TransporterDto dto) {
        Transporter t = new Transporter();
        t.setCompanyName(dto.getCompanyName());
        t.setRating(dto.getRating());
        // map available trucks
        if (dto.getAvailableTrucks() != null) {
            List<Transporter.TruckCapacity> list = new ArrayList<>();
            for (TransporterDto.TruckCapacityDto tc : dto.getAvailableTrucks()) {
                Transporter.TruckCapacity e = new Transporter.TruckCapacity();
                e.setTruckType(tc.getTruckType());
                e.setCount(tc.getCount());
                list.add(e);
            }
            t.setAvailableTrucks(list);
        }
        Transporter saved = transporterRepository.save(t);
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TransporterDto getTransporter(UUID transporterId) {
        Transporter t = transporterRepository.findById(transporterId).orElseThrow(() -> new ResourceNotFoundException("Transporter not found"));
        return toDto(t);
    }

    @Override
    @Transactional
    public TransporterDto updateTrucks(UUID transporterId, TransporterDto dto) {
        Transporter t = transporterRepository.findById(transporterId).orElseThrow(() -> new ResourceNotFoundException("Transporter not found"));
        List<Transporter.TruckCapacity> newList = new ArrayList<>();
        if (dto.getAvailableTrucks() != null) {
            for (TransporterDto.TruckCapacityDto tc : dto.getAvailableTrucks()) {
                Transporter.TruckCapacity e = new Transporter.TruckCapacity();
                e.setTruckType(tc.getTruckType());
                e.setCount(tc.getCount());
                newList.add(e);
            }
        }
        t.setAvailableTrucks(newList);
        Transporter saved = transporterRepository.save(t);
        return toDto(saved);
    }

    private TransporterDto toDto(Transporter t) {
        TransporterDto dto = new TransporterDto();
        dto.setTransporterId(t.getTransporterId());
        dto.setCompanyName(t.getCompanyName());
        dto.setRating(t.getRating());
        List<TransporterDto.TruckCapacityDto> list = new ArrayList<>();
        if (t.getAvailableTrucks() != null) {
            for (Transporter.TruckCapacity e : t.getAvailableTrucks()) {
                TransporterDto.TruckCapacityDto tc = new TransporterDto.TruckCapacityDto();
                tc.setTruckType(e.getTruckType());
                tc.setCount(e.getCount());
                list.add(tc);
            }
        }
        dto.setAvailableTrucks(list);
        return dto;
    }
}

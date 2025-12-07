package com.tms.service.impl;

import com.tms.dto.LoadDto;
import com.tms.entity.Load;
import com.tms.exception.InvalidStatusTransitionException;
import com.tms.exception.ResourceNotFoundException;
import com.tms.repository.LoadRepository;
import com.tms.service.LoadService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class LoadServiceImpl implements LoadService {

    private final LoadRepository loadRepository;

    public LoadServiceImpl(LoadRepository loadRepository) {
        this.loadRepository = loadRepository;
    }

    @Override
    @Transactional
    public LoadDto createLoad(LoadDto loadDto) {
        Load load = new Load();
        load.setShipperId(loadDto.getShipperId());
        load.setLoadingCity(loadDto.getLoadingCity());
        load.setUnloadingCity(loadDto.getUnloadingCity());
        load.setLoadingDate(loadDto.getLoadingDate());
        load.setProductType(loadDto.getProductType());
        load.setWeight(loadDto.getWeight());
        load.setWeightUnit(loadDto.getWeightUnit());
        load.setTruckType(loadDto.getTruckType());
        load.setNoOfTrucks(loadDto.getNoOfTrucks());
        load.setStatus("POSTED");
        load.setDatePosted(Instant.now());

        Load saved = loadRepository.save(load);
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public LoadDto getLoad(UUID loadId) {
        Load load = loadRepository.findById(loadId).orElseThrow(() -> new ResourceNotFoundException("Load not found"));
        return toDto(load);
    }

    @Override
    @Transactional
    public LoadDto cancelLoad(UUID loadId) {
        Load load = loadRepository.findById(loadId).orElseThrow(() -> new ResourceNotFoundException("Load not found"));
        if ("BOOKED".equals(load.getStatus())) {
            throw new InvalidStatusTransitionException("Cannot cancel a booked load");
        }
        load.setStatus("CANCELLED");
        Load saved = loadRepository.save(load);
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LoadDto> listLoads(String shipperId, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Load> p;
        if (shipperId != null && status != null) {
            p = loadRepository.findByShipperIdAndStatus(shipperId, status, pageable);
        } else if (shipperId != null) {
            p = loadRepository.findByShipperId(shipperId, pageable);
        } else if (status != null) {
            p = loadRepository.findByStatus(status, pageable);
        } else {
            p = loadRepository.findAll(pageable);
        }
        return p.map(this::toDto);
    }

    private LoadDto toDto(Load load) {
        LoadDto dto = new LoadDto();
        dto.setLoadId(load.getLoadId());
        dto.setShipperId(load.getShipperId());
        dto.setLoadingCity(load.getLoadingCity());
        dto.setUnloadingCity(load.getUnloadingCity());
        dto.setLoadingDate(load.getLoadingDate());
        dto.setProductType(load.getProductType());
        dto.setWeight(load.getWeight());
        dto.setWeightUnit(load.getWeightUnit());
        dto.setTruckType(load.getTruckType());
        dto.setNoOfTrucks(load.getNoOfTrucks());
        dto.setStatus(load.getStatus());
        dto.setDatePosted(load.getDatePosted());
        return dto;
    }
}

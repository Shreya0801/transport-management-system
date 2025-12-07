package com.tms.service;

import com.tms.dto.LoadDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface LoadService {
    LoadDto createLoad(LoadDto loadDto);
    LoadDto getLoad(UUID loadId);
    LoadDto cancelLoad(UUID loadId);
    Page<LoadDto> listLoads(String shipperId, String status, int page, int size);
}

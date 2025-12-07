package com.tms.service;

import com.tms.dto.TransporterDto;
import java.util.UUID;

public interface TransporterService {
    TransporterDto register(TransporterDto dto);
    TransporterDto getTransporter(UUID transporterId);
    TransporterDto updateTrucks(UUID transporterId, TransporterDto dto);
}


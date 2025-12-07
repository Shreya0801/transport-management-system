package com.tms.controller;

import com.tms.dto.TransporterDto;
import com.tms.service.TransporterService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/transporter")
public class TransporterController {

    private final TransporterService transporterService;

    public TransporterController(TransporterService transporterService) {
        this.transporterService = transporterService;
    }

    @PostMapping
    public TransporterDto register(@RequestBody TransporterDto dto) {
        return transporterService.register(dto);
    }

    @GetMapping("/{transporterId}")
    public TransporterDto get(@PathVariable UUID transporterId) {
        return transporterService.getTransporter(transporterId);
    }

    @PutMapping("/{transporterId}/trucks")
    public TransporterDto updateTrucks(@PathVariable UUID transporterId, @RequestBody TransporterDto dto) {
        return transporterService.updateTrucks(transporterId, dto);
    }
}


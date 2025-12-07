package com.tms.controller;

import com.tms.dto.LoadDto;
import com.tms.service.LoadService;
import com.tms.service.BidService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/load")
public class LoadController {

    private final LoadService loadService;
    private final BidService bidService;

    public LoadController(LoadService loadService, BidService bidService) {
        this.loadService = loadService;
        this.bidService = bidService;
    }

    @PostMapping
    public LoadDto create(@RequestBody LoadDto dto) {
        return loadService.createLoad(dto);
    }

    @GetMapping
    public Page<LoadDto> list(@RequestParam(required = false) String shipperId,
                              @RequestParam(required = false) String status,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size) {
        return loadService.listLoads(shipperId, status, page, size);
    }

    @GetMapping("/{loadId}")
    public LoadDto get(@PathVariable UUID loadId) {
        return loadService.getLoad(loadId);
    }

    @PatchMapping("/{loadId}/cancel")
    public LoadDto cancel(@PathVariable UUID loadId) {
        return loadService.cancelLoad(loadId);
    }

    @GetMapping("/{loadId}/best-bids")
    public List<?> bestBids(@PathVariable UUID loadId) {
        return bidService.getBestBids(loadId);
    }
}

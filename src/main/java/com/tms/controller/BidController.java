package com.tms.controller;

import com.tms.dto.BidDto;
import com.tms.service.BidService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bid")
public class BidController {

    private final BidService bidService;

    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    @PostMapping
    public BidDto submit(@RequestBody BidDto dto) {
        return bidService.submitBid(dto);
    }

    @GetMapping
    public List<BidDto> list(@RequestParam(required = false) UUID loadId,
                              @RequestParam(required = false) UUID transporterId,
                              @RequestParam(required = false) String status) {
        return bidService.findByFilter(loadId, transporterId, status);
    }

    @GetMapping("/{bidId}")
    public BidDto get(@PathVariable UUID bidId) {
        return bidService.getBid(bidId);
    }

    @PatchMapping("/{bidId}/reject")
    public BidDto reject(@PathVariable UUID bidId) {
        return bidService.rejectBid(bidId);
    }
}


package com.tms.service;

import com.tms.dto.BidDto;
import java.util.List;
import java.util.UUID;

public interface BidService {
    BidDto submitBid(BidDto bidDto);
    BidDto getBid(UUID bidId);
    List<BidDto> findByFilter(UUID loadId, UUID transporterId, String status);
    BidDto rejectBid(UUID bidId);
    List<BidDto> getBestBids(UUID loadId);
}


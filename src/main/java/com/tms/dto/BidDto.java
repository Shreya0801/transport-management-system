package com.tms.dto;

import java.time.Instant;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BidDto {
    private UUID bidId;
    private UUID loadId;
    private UUID transporterId;
    private double proposedRate;
    private int trucksOffered;
    private String status;
    private Instant submittedAt;
}

package com.tms.dto;

import java.time.Instant;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingDto {
    private UUID bookingId;
    private UUID loadId;
    private UUID bidId;
    private UUID transporterId;
    private int allocatedTrucks;
    private double finalRate;
    private String status;
    private Instant bookedAt;
}

package com.tms.dto;

import java.time.Instant;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoadDto {
    private UUID loadId;
    private String shipperId;
    private String loadingCity;
    private String unloadingCity;
    private Instant loadingDate;
    private String productType;
    private double weight;
    private String weightUnit;
    private String truckType;
    private int noOfTrucks;
    private String status;
    private Instant datePosted;
}

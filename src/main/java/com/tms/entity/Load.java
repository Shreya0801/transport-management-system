package com.tms.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;

@Entity
@Table(name = "loads", indexes = {
    @Index(name = "idx_load_shipper_status", columnList = "shipperId, status"),
    @Index(name = "idx_load_loadId", columnList = "loadId")
})
@Data
public class Load {
    @Id
    @GeneratedValue
    private UUID loadId;

    @Column(nullable = false)
    private String shipperId;

    @Column(nullable = false)
    private String loadingCity;

    @Column(nullable = false)
    private String unloadingCity;

    @Column(nullable = false)
    private Instant loadingDate;

    @Column(nullable = false)
    private String productType;

    @Column(nullable = false)
    private double weight;

    @Column(nullable = false)
    private String weightUnit; // KG | TON

    @Column(nullable = false)
    private String truckType;

    @Column(nullable = false)
    private int noOfTrucks;

    @Column(nullable = false)
    private String status; // POSTED | OPEN_FOR_BIDS | BOOKED | CANCELLED

    @Column(nullable = false)
    private Instant datePosted;

    @Version
    private Long version;

    @OneToMany(mappedBy = "load", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Bid> bids;

    @OneToMany(mappedBy = "load", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Booking> bookings;
}

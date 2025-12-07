package com.tms.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;

@Entity
@Table(name = "bookings", indexes = {
    @Index(name = "idx_booking_bookingId", columnList = "bookingId"),
    @Index(name = "idx_booking_loadId", columnList = "load_id"),
    @Index(name = "idx_booking_transporterId", columnList = "transporter_id")
})
@Data
public class Booking {
    @Id
    @GeneratedValue
    private UUID bookingId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "load_id")
    private Load load;

    @ManyToOne(optional = false)
    @JoinColumn(name = "bid_id")
    private Bid bid;

    @ManyToOne(optional = false)
    @JoinColumn(name = "transporter_id")
    private Transporter transporter;

    @Column(nullable = false)
    private int allocatedTrucks;

    @Column(nullable = false)
    private double finalRate;

    @Column(nullable = false)
    private String status; // CONFIRMED | COMPLETED | CANCELLED

    @Column(nullable = false)
    private Instant bookedAt;
}

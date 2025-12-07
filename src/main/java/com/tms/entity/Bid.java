package com.tms.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bids", indexes = {
    @Index(name = "idx_bid_load_transporter_status", columnList = "load_id, transporter_id, status"),
    @Index(name = "idx_bid_bidId", columnList = "bidId")
})
@Data
public class Bid {
    @Id
    @GeneratedValue
    private UUID bidId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "load_id")
    private Load load;

    @ManyToOne(optional = false)
    @JoinColumn(name = "transporter_id")
    private Transporter transporter;

    @Column(nullable = false)
    private double proposedRate;

    @Column(nullable = false)
    private int trucksOffered;

    @Column(nullable = false)
    private String status; // PENDING | ACCEPTED | REJECTED

    @Column(nullable = false)
    private Instant submittedAt;
}

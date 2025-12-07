package com.tms.entity;

import jakarta.persistence.*;
import java.util.UUID;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "transporters", indexes = {
    @Index(name = "idx_transporter_transporterId", columnList = "transporterId")
})
@Data
@Getter
@Setter
public class Transporter {
    @Id
    @GeneratedValue
    private UUID transporterId;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private double rating;

    @ElementCollection
    @CollectionTable(name = "transporter_trucks", joinColumns = @JoinColumn(name = "transporter_id"))
    private List<TruckCapacity> availableTrucks;

    @Embeddable
    @Data
    public static class TruckCapacity {
        private String truckType;
        private int count;
    }
}

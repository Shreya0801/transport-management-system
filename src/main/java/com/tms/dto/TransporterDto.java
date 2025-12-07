package com.tms.dto;

import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransporterDto {
    private UUID transporterId;
    private String companyName;
    private double rating;
    private List<TruckCapacityDto> availableTrucks;

    public static class TruckCapacityDto {
        private String truckType;
        private int count;
        public String getTruckType() { return truckType; }
        public void setTruckType(String truckType) { this.truckType = truckType; }
        public int getCount() { return count; }
        public void setCount(int count) { this.count = count; }
    }
}

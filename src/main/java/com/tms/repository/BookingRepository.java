package com.tms.repository;

import com.tms.entity.Booking;
import com.tms.entity.Load;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    List<Booking> findByLoad(Load load);
}

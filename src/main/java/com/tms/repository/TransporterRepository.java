package com.tms.repository;

import com.tms.entity.Transporter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TransporterRepository extends JpaRepository<Transporter, UUID> {
    // Custom query methods as needed
}


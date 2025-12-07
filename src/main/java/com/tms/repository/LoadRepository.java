package com.tms.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tms.entity.Load;

// Custom query methods for listing with pagination and filters
public interface LoadRepository extends JpaRepository<Load, UUID> {
    Page<Load> findByShipperId(String shipperId, Pageable pageable);
    Page<Load> findByStatus(String status, Pageable pageable);
    Page<Load> findByShipperIdAndStatus(String shipperId, String status, Pageable pageable);
}

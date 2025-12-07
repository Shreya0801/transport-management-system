package com.tms.repository;

import com.tms.entity.Bid;
import com.tms.entity.Load;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BidRepository extends JpaRepository<Bid, UUID> {
    List<Bid> findByLoad(Load load);
    List<Bid> findByLoadAndStatus(Load load, String status);
}

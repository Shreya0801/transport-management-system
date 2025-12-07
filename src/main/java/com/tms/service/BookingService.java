package com.tms.service;

import com.tms.dto.BookingDto;
import java.util.UUID;

public interface BookingService {
    BookingDto acceptBid(UUID bidId, int allocateTrucks);
    BookingDto getBooking(UUID bookingId);
    BookingDto cancelBooking(UUID bookingId);
}


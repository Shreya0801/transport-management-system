package com.tms.controller;

import com.tms.dto.BookingDto;
import com.tms.service.BookingService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/booking")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto acceptBid(@RequestParam UUID bidId, @RequestParam int allocateTrucks) {
        return bookingService.acceptBid(bidId, allocateTrucks);
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(@PathVariable UUID bookingId) {
        return bookingService.getBooking(bookingId);
    }

    @PatchMapping("/{bookingId}/cancel")
    public BookingDto cancel(@PathVariable UUID bookingId) {
        return bookingService.cancelBooking(bookingId);
    }
}


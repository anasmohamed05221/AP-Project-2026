package com.sb.stayeaseap.validator;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class BookingValidator {

    public void validate(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        if (roomId == null) {
            throw new IllegalArgumentException("Room is required.");
        }
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Check-in and check-out dates are required.");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out must be after check-in.");
        }
    }
}
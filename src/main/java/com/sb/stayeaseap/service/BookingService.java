package com.sb.stayeaseap.service;

import com.sb.stayeaseap.model.Booking;
import com.sb.stayeaseap.model.Room;
import com.sb.stayeaseap.model.User;
import com.sb.stayeaseap.repository.BookingRepository;
import com.sb.stayeaseap.repository.RoomRepository;
import com.sb.stayeaseap.repository.UserRepository;
import com.sb.stayeaseap.validator.BookingValidator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final BookingValidator bookingValidator;

    public BookingService(BookingRepository bookingRepository,
                          RoomRepository roomRepository,
                          UserRepository userRepository,
                          BookingValidator bookingValidator) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.bookingValidator = bookingValidator;
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    public List<Booking> getBookingsByUser(String email) {
        return bookingRepository.findByUserEmailOrderByCreatedAtDesc(email);
    }

    public Booking createBooking(Long roomId, LocalDate checkIn, LocalDate checkOut, String userEmail) {
        bookingValidator.validate(roomId, checkIn, checkOut);

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (!room.getIsAvailable()) {
            throw new IllegalArgumentException("This room is not available.");
        }

        if (bookingRepository.existsOverlappingBooking(roomId, checkIn, checkOut)) {
            throw new IllegalArgumentException("This room is already booked for the selected dates.");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckIn(checkIn);
        booking.setCheckOut(checkOut);

        return bookingRepository.save(booking);
    }

    public long calculateNights(LocalDate checkIn, LocalDate checkOut) {
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    public BigDecimal calculateBasePrice(Room room, long nights) {
        return room.getPricePerNight().multiply(BigDecimal.valueOf(nights));
    }

    public BigDecimal calculateServiceFee(BigDecimal basePrice) {
        return basePrice.multiply(new BigDecimal("0.12")).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateTotal(BigDecimal basePrice, BigDecimal serviceFee) {
        return basePrice.add(new BigDecimal("45")).add(serviceFee);
    }
}
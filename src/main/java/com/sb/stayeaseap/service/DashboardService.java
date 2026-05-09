package com.sb.stayeaseap.service;

import com.sb.stayeaseap.model.Booking;
import com.sb.stayeaseap.repository.BookingRepository;
import com.sb.stayeaseap.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class DashboardService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public DashboardService(BookingRepository bookingRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    public String getUserName(String email) {
        return userRepository.findByEmail(email)
                .map(u -> u.getName())
                .orElse(email);
    }

    public List<Booking> getBookings(String email) {
        return bookingRepository.findByUserEmailOrderByCreatedAtDesc(email);
    }

    public long countTotal(String email) {
        return bookingRepository.findByUserEmailOrderByCreatedAtDesc(email).size();
    }

    public long countUpcoming(String email) {
        return bookingRepository.countUpcomingByUserEmail(email, LocalDate.now());
    }

    public long countPast(String email) {
        return bookingRepository.countPastByUserEmail(email, LocalDate.now());
    }

    public String getDisplayStatus(Booking b) {
        LocalDate today = LocalDate.now();
        if ("cancelled".equals(b.getStatus())) return "cancelled";
        if (b.getCheckOut().isBefore(today))   return "past";
        if (!b.getCheckIn().isAfter(today))    return "active";
        return "upcoming";
    }

    public long getNights(Booking b) {
        return Math.max(1, ChronoUnit.DAYS.between(b.getCheckIn(), b.getCheckOut()));
    }

    public BigDecimal getTotalPrice(Booking b) {
        return b.getRoom().getPricePerNight().multiply(BigDecimal.valueOf(getNights(b)));
    }

    public boolean canCancel(Booking b) {
        return !"cancelled".equals(b.getStatus()) && b.getCheckIn().isAfter(LocalDate.now());
    }
}
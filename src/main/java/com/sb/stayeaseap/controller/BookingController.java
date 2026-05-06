package com.sb.stayeaseap.controller;

import com.sb.stayeaseap.model.Booking;
import com.sb.stayeaseap.model.Room;
import com.sb.stayeaseap.service.BookingService;
import com.sb.stayeaseap.service.HotelService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
public class BookingController {

    private final BookingService bookingService;
    private final HotelService hotelService;

    public BookingController(BookingService bookingService, HotelService hotelService) {
        this.bookingService = bookingService;
        this.hotelService = hotelService;
    }

    @GetMapping("/booking")
    public String bookingForm(@RequestParam Long roomId,
                              @RequestParam(required = false) String checkIn,
                              @RequestParam(required = false) String checkOut,
                              Model model) {
        Room room = hotelService.getRoomById(roomId);

        LocalDate checkInDate = (checkIn != null && !checkIn.isEmpty()) ? LocalDate.parse(checkIn) : null;
        LocalDate checkOutDate = (checkOut != null && !checkOut.isEmpty()) ? LocalDate.parse(checkOut) : null;

        long nights = 0;
        BigDecimal basePrice = BigDecimal.ZERO;
        BigDecimal serviceFee = BigDecimal.ZERO;
        BigDecimal cleaningFee = new BigDecimal("45");
        BigDecimal total = BigDecimal.ZERO;

        if (checkInDate != null && checkOutDate != null && checkOutDate.isAfter(checkInDate)) {
            nights = bookingService.calculateNights(checkInDate, checkOutDate);
            basePrice = bookingService.calculateBasePrice(room, nights);
            serviceFee = bookingService.calculateServiceFee(basePrice);
            total = bookingService.calculateTotal(basePrice, serviceFee);
        }

        model.addAttribute("room", room);
        model.addAttribute("checkIn", checkIn);
        model.addAttribute("checkOut", checkOut);
        model.addAttribute("nights", nights);
        model.addAttribute("basePrice", basePrice);
        model.addAttribute("serviceFee", serviceFee);
        model.addAttribute("cleaningFee", cleaningFee);
        model.addAttribute("total", total);

        return "booking/booking";
    }

    @PostMapping("/booking")
    public String createBooking(@RequestParam Long roomId,
                                @RequestParam String checkIn,
                                @RequestParam String checkOut,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) {
        try {
            Booking booking = bookingService.createBooking(
                    roomId,
                    LocalDate.parse(checkIn),
                    LocalDate.parse(checkOut),
                    userDetails.getUsername()
            );
            return "redirect:/booking/confirm/" + booking.getId();
        } catch (IllegalArgumentException e) {
            Room room = hotelService.getRoomById(roomId);
            model.addAttribute("room", room);
            model.addAttribute("checkIn", checkIn);
            model.addAttribute("checkOut", checkOut);
            model.addAttribute("error", e.getMessage());
            return "booking/booking";
        }
    }

    @GetMapping("/booking/confirm/{id}")
    public String bookingConfirm(@PathVariable Long id,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 Model model) {
        Booking booking = bookingService.getBookingById(id);

        if (!booking.getUser().getEmail().equals(userDetails.getUsername())) {
            return "redirect:/";
        }

        long nights = bookingService.calculateNights(booking.getCheckIn(), booking.getCheckOut());
        BigDecimal basePrice = bookingService.calculateBasePrice(booking.getRoom(), nights);
        BigDecimal serviceFee = bookingService.calculateServiceFee(basePrice);
        BigDecimal cleaningFee = new BigDecimal("45");
        BigDecimal total = bookingService.calculateTotal(basePrice, serviceFee);

        model.addAttribute("booking", booking);
        model.addAttribute("nights", nights);
        model.addAttribute("basePrice", basePrice);
        model.addAttribute("serviceFee", serviceFee);
        model.addAttribute("cleaningFee", cleaningFee);
        model.addAttribute("total", total);

        return "booking/booking-confirm";
    }
}
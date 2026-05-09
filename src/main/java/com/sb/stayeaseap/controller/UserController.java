package com.sb.stayeaseap.controller;

import com.sb.stayeaseap.model.Booking;
import com.sb.stayeaseap.service.BookingService;
import com.sb.stayeaseap.service.DashboardService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dashboard")
public class UserController {

    private final DashboardService dashboardService;
    private final BookingService bookingService;

    public UserController(DashboardService dashboardService, BookingService bookingService) {
        this.dashboardService = dashboardService;
        this.bookingService = bookingService;
    }

    @GetMapping("")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String email = userDetails.getUsername();
        model.addAttribute("totalBookings", dashboardService.countTotal(email));
        model.addAttribute("upcomingBookings", dashboardService.countUpcoming(email));
        model.addAttribute("pastStays", dashboardService.countPast(email));
        model.addAttribute("userName", dashboardService.getUserName(email));
        return "user/dashboard";
    }

    @GetMapping("/bookings")
    public String myBookings(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String email = userDetails.getUsername();
        List<Booking> bookings = dashboardService.getBookings(email);

        Map<Long, String>     displayStatuses = new HashMap<>();
        Map<Long, Long>       nights          = new HashMap<>();
        Map<Long, BigDecimal> totalPrices     = new HashMap<>();
        Map<Long, Boolean>    canCancels      = new HashMap<>();

        for (Booking b : bookings) {
            displayStatuses.put(b.getId(), dashboardService.getDisplayStatus(b));
            nights.put(b.getId(),          dashboardService.getNights(b));
            totalPrices.put(b.getId(),     dashboardService.getTotalPrice(b));
            canCancels.put(b.getId(),      dashboardService.canCancel(b));
        }

        model.addAttribute("bookings",        bookings);
        model.addAttribute("displayStatuses", displayStatuses);
        model.addAttribute("nights",          nights);
        model.addAttribute("totalPrices",     totalPrices);
        model.addAttribute("canCancels",      canCancels);
        model.addAttribute("totalBookings",   bookings.size());
        model.addAttribute("upcomingBookings", dashboardService.countUpcoming(email));
        model.addAttribute("pastStays",       dashboardService.countPast(email));

        return "user/my-bookings";
    }

    @PostMapping("/bookings/{id}/cancel")
    public String cancelBooking(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails userDetails) {
        Booking booking = bookingService.getBookingById(id);
        if (!booking.getUser().getEmail().equals(userDetails.getUsername())) {
            return "redirect:/dashboard/bookings";
        }
        if (dashboardService.canCancel(booking)) {
            bookingService.cancelBooking(id);
        }
        return "redirect:/dashboard/bookings";
    }
}
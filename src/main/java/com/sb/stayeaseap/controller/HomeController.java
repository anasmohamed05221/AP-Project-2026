package com.sb.stayeaseap.controller;

import com.sb.stayeaseap.model.Hotel;
import com.sb.stayeaseap.service.HotelService;
import com.sb.stayeaseap.service.ReviewService;
import com.sb.stayeaseap.service.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    private final HotelService hotelService;
    private final SearchService searchService;
    private final ReviewService reviewService;

    public HomeController(HotelService hotelService, SearchService searchService, ReviewService reviewService) {
        this.hotelService = hotelService;
        this.searchService = searchService;
        this.reviewService = reviewService;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Hotel> hotels = hotelService.getFeaturedHotels();
        model.addAttribute("hotels", hotels);
        model.addAttribute("minPrices", searchService.buildMinPrices(hotels));
        return "home/index";
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false, defaultValue = "") String city,
                         @RequestParam(name = "check_in", required = false, defaultValue = "") String checkIn,
                         @RequestParam(name = "check_out", required = false, defaultValue = "") String checkOut,
                         @RequestParam(required = false, defaultValue = "rating") String sort,
                         @RequestParam(name = "max_price", required = false) Integer maxPrice,
                         @RequestParam(required = false) List<Integer> stars,
                         @RequestParam(required = false, defaultValue = "1") int page,
                         Model model) {

        int resolvedMax = maxPrice != null ? maxPrice : 1000;

        LocalDate parsedCheckIn  = (checkIn  != null && !checkIn.isEmpty())  ? LocalDate.parse(checkIn)  : null;
        LocalDate parsedCheckOut = (checkOut != null && !checkOut.isEmpty()) ? LocalDate.parse(checkOut) : null;

        List<Hotel> hotels = searchService.search(city, stars, resolvedMax, sort, page, parsedCheckIn, parsedCheckOut);
        int totalPages     = searchService.totalPages(city, stars, resolvedMax, parsedCheckIn, parsedCheckOut);

        Map<Long, BigDecimal> minPrices    = searchService.buildMinPrices(hotels);
        Map<Long, Double>     avgRatings   = new HashMap<>();
        Map<Long, Long>       reviewCounts = new HashMap<>();
        for (Hotel h : hotels) {
            avgRatings.put(h.getId(),   reviewService.getAvgRating(h.getId()));
            reviewCounts.put(h.getId(), reviewService.getReviewCount(h.getId()));
        }

        model.addAttribute("hotels", hotels);
        model.addAttribute("minPrices", minPrices);
        model.addAttribute("avgRatings", avgRatings);
        model.addAttribute("reviewCounts", reviewCounts);
        model.addAttribute("city", city);
        model.addAttribute("checkIn", checkIn);
        model.addAttribute("checkOut", checkOut);
        model.addAttribute("sort", sort);
        model.addAttribute("maxPrice", resolvedMax);
        model.addAttribute("selectedStars", stars != null ? stars : List.of(3, 4, 5));
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "home/search-results";
    }
}

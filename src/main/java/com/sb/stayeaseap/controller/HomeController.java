package com.sb.stayeaseap.controller;

import com.sb.stayeaseap.model.Hotel;
import com.sb.stayeaseap.service.HotelService;
import com.sb.stayeaseap.service.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    private final HotelService hotelService;
    private final SearchService searchService;

    public HomeController(HotelService hotelService, SearchService searchService) {
        this.hotelService = hotelService;
        this.searchService = searchService;
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
                         @RequestParam(required = false, defaultValue = "") String checkIn,
                         @RequestParam(required = false, defaultValue = "") String checkOut,
                         @RequestParam(required = false, defaultValue = "rating") String sort,
                         @RequestParam(required = false) Integer maxPrice,
                         @RequestParam(required = false) List<Integer> stars,
                         Model model) {

        List<Hotel> hotels = searchService.search(city, stars, maxPrice, sort);

        model.addAttribute("hotels", hotels);
        model.addAttribute("minPrices", searchService.buildMinPrices(hotels));
        model.addAttribute("city", city);
        model.addAttribute("checkIn", checkIn);
        model.addAttribute("checkOut", checkOut);
        model.addAttribute("sort", sort);
        model.addAttribute("maxPrice", maxPrice != null ? maxPrice : 1000);
        model.addAttribute("selectedStars", stars != null ? stars : List.of(3, 4, 5));
        return "home/search-results";
    }
}

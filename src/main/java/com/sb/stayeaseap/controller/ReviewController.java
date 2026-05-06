package com.sb.stayeaseap.controller;

import com.sb.stayeaseap.service.HotelService;
import com.sb.stayeaseap.service.ReviewService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ReviewController {

    private final ReviewService reviewService;
    private final HotelService hotelService;

    public ReviewController(ReviewService reviewService, HotelService hotelService) {
        this.reviewService = reviewService;
        this.hotelService = hotelService;
    }

    @GetMapping("/reviews")
    public String reviews(@RequestParam(defaultValue = "1") Long hotelId,
                          @AuthenticationPrincipal UserDetails userDetails,
                          Model model) {
        model.addAttribute("hotel", hotelService.getHotelById(hotelId));
        model.addAttribute("reviews", reviewService.getReviewsByHotel(hotelId));
        model.addAttribute("avgRating", reviewService.getAvgRating(hotelId));
        model.addAttribute("reviewCount", reviewService.getReviewCount(hotelId));
        model.addAttribute("recommendPct", reviewService.getRecommendPct(hotelId));
        model.addAttribute("hotelId", hotelId);

        boolean isLoggedIn = userDetails != null;
        model.addAttribute("isLoggedIn", isLoggedIn);

        if (isLoggedIn) {
            String email = userDetails.getUsername();
            boolean alreadyReviewed = reviewService.hasUserReviewed(email, hotelId);
            boolean hasStayed = reviewService.hasUserStayed(email, hotelId);
            model.addAttribute("alreadyReviewed", alreadyReviewed);
            model.addAttribute("hasStayed", hasStayed);
            if (hasStayed && !alreadyReviewed) {
                model.addAttribute("pastStays", reviewService.getPastStays(email, hotelId));
            }
        }

        return "reviews/reviews";
    }

    @PostMapping("/reviews/submit")
    public String submitReview(@RequestParam Long hotelId,
                               @RequestParam Integer rating,
                               @RequestParam String comment,
                               @AuthenticationPrincipal UserDetails userDetails,
                               Model model) {
        try {
            reviewService.submitReview(hotelId, rating, comment, userDetails.getUsername());
            return "redirect:/reviews?hotelId=" + hotelId + "&success=1";
        } catch (IllegalArgumentException e) {
            model.addAttribute("hotel", hotelService.getHotelById(hotelId));
            model.addAttribute("reviews", reviewService.getReviewsByHotel(hotelId));
            model.addAttribute("avgRating", reviewService.getAvgRating(hotelId));
            model.addAttribute("reviewCount", reviewService.getReviewCount(hotelId));
            model.addAttribute("recommendPct", reviewService.getRecommendPct(hotelId));
            model.addAttribute("hotelId", hotelId);
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("alreadyReviewed", reviewService.hasUserReviewed(userDetails.getUsername(), hotelId));
            model.addAttribute("hasStayed", reviewService.hasUserStayed(userDetails.getUsername(), hotelId));
            model.addAttribute("pastStays", reviewService.getPastStays(userDetails.getUsername(), hotelId));
            model.addAttribute("error", e.getMessage());
            return "reviews/reviews";
        }
    }

    @GetMapping("/about")
    public String about(@RequestParam(required = false) String success, Model model) {
        model.addAttribute("success", "1".equals(success));
        return "reviews/about";
    }

    @PostMapping("/contact")
    public String contact(@RequestParam String name,
                          @RequestParam String email,
                          @RequestParam String message) {
        reviewService.saveContact(name, email, message);
        return "redirect:/about?success=1";
    }
}

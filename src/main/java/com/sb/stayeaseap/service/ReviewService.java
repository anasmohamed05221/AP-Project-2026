package com.sb.stayeaseap.service;

import com.sb.stayeaseap.model.Booking;
import com.sb.stayeaseap.model.Contact;
import com.sb.stayeaseap.model.Hotel;
import com.sb.stayeaseap.model.Review;
import com.sb.stayeaseap.model.User;
import com.sb.stayeaseap.repository.BookingRepository;
import com.sb.stayeaseap.repository.ContactRepository;
import com.sb.stayeaseap.repository.HotelRepository;
import com.sb.stayeaseap.repository.ReviewRepository;
import com.sb.stayeaseap.repository.UserRepository;
import com.sb.stayeaseap.Validator.ReviewValidator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ContactRepository contactRepository;
    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;
    private final ReviewValidator reviewValidator;

    public ReviewService(ReviewRepository reviewRepository,
                         ContactRepository contactRepository,
                         BookingRepository bookingRepository,
                         HotelRepository hotelRepository,
                         UserRepository userRepository,
                         ReviewValidator reviewValidator) {
        this.reviewRepository = reviewRepository;
        this.contactRepository = contactRepository;
        this.bookingRepository = bookingRepository;
        this.hotelRepository = hotelRepository;
        this.userRepository = userRepository;
        this.reviewValidator = reviewValidator;
    }

    public List<Review> getReviewsByHotel(Long hotelId) {
        return reviewRepository.findTop4ByHotelIdOrderByCreatedAtDesc(hotelId);
    }

    public Double getAvgRating(Long hotelId) {
        return reviewRepository.findAvgRatingByHotelId(hotelId);
    }

    public long getReviewCount(Long hotelId) {
        return reviewRepository.countByHotelId(hotelId);
    }

    public int getRecommendPct(Long hotelId) {
        long total = reviewRepository.countByHotelId(hotelId);
        if (total == 0) return 0;
        long positive = reviewRepository.countPositiveByHotelId(hotelId);
        return (int) (positive * 100 / total);
    }

    public boolean hasUserReviewed(String email, Long hotelId) {
        return reviewRepository.existsByUserEmailAndHotelId(email, hotelId);
    }

    public boolean hasUserStayed(String email, Long hotelId) {
        return !bookingRepository.findPastStaysByUserAndHotel(email, hotelId, LocalDate.now()).isEmpty();
    }

    public List<Booking> getPastStays(String email, Long hotelId) {
        return bookingRepository.findPastStaysByUserAndHotel(email, hotelId, LocalDate.now());
    }

    public void submitReview(Long hotelId, Integer rating, String comment, String userEmail) {
        reviewValidator.validate(rating, comment);

        if (reviewRepository.existsByUserEmailAndHotelId(userEmail, hotelId)) {
            throw new IllegalArgumentException("You have already reviewed this hotel.");
        }

        List<Booking> pastStays = bookingRepository.findPastStaysByUserAndHotel(userEmail, hotelId, LocalDate.now());
        if (pastStays.isEmpty()) {
            throw new IllegalArgumentException("You can only review hotels you have stayed at.");
        }

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Review review = new Review();
        review.setHotel(hotel);
        review.setUser(user);
        review.setRating(rating);
        review.setComment(comment);

        reviewRepository.save(review);
    }

    public void saveContact(String name, String email, String message) {
        Contact contact = new Contact();
        contact.setName(name);
        contact.setEmail(email);
        contact.setMessage(message);
        contactRepository.save(contact);
    }
}

package com.sb.stayeaseap.repository;

import com.sb.stayeaseap.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findTop4ByHotelIdOrderByCreatedAtDesc(Long hotelId);

    boolean existsByUserEmailAndHotelId(String email, Long hotelId);

    long countByHotelId(Long hotelId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.hotel.id = :hotelId")
    Double findAvgRatingByHotelId(@Param("hotelId") Long hotelId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.hotel.id = :hotelId AND r.rating >= 4")
    long countPositiveByHotelId(@Param("hotelId") Long hotelId);
}
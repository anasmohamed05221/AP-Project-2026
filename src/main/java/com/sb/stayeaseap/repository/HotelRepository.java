package com.sb.stayeaseap.repository;

import com.sb.stayeaseap.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    @Query("SELECT h FROM Hotel h WHERE " +
           "LOWER(h.city) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(h.location) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(h.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Hotel> searchByKeyword(@Param("keyword") String keyword);

    List<Hotel> findTop4ByOrderByStarsDesc();
}
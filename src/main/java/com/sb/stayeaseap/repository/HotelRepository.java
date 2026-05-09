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

    @Query(value = "SELECT h.* FROM hotels h " +
                   "LEFT JOIN rooms r ON r.hotel_id = h.id AND r.is_available = true " +
                   "LEFT JOIN reviews rv ON rv.hotel_id = h.id " +
                   "GROUP BY h.id " +
                   "ORDER BY AVG(rv.rating) DESC NULLS LAST, RANDOM() " +
                   "LIMIT 4", nativeQuery = true)
    List<Hotel> findFeatured();
}
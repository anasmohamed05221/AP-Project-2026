package com.sb.stayeaseap.repository;

import com.sb.stayeaseap.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByHotelId(Long hotelId);

    @Query("SELECT MIN(r.pricePerNight) FROM Room r WHERE r.hotel.id = :hotelId AND r.isAvailable = true")
    Optional<BigDecimal> findMinPriceByHotelId(@Param("hotelId") Long hotelId);
}
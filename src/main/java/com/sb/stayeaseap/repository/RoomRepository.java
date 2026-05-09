package com.sb.stayeaseap.repository;

import com.sb.stayeaseap.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByHotelId(Long hotelId);

    @Query("SELECT MIN(r.pricePerNight) FROM Room r WHERE r.hotel.id = :hotelId AND r.isAvailable = true")
    Optional<BigDecimal> findMinPriceByHotelId(@Param("hotelId") Long hotelId);

    @Query("SELECT COUNT(r) > 0 FROM Room r WHERE r.hotel.id = :hotelId AND r.isAvailable = true " +
           "AND NOT EXISTS (SELECT b FROM Booking b WHERE b.room.id = r.id " +
           "AND b.status != 'cancelled' AND b.checkIn < :checkOut AND b.checkOut > :checkIn)")
    boolean existsAvailableRoomForDates(@Param("hotelId") Long hotelId,
                                        @Param("checkIn") LocalDate checkIn,
                                        @Param("checkOut") LocalDate checkOut);
}
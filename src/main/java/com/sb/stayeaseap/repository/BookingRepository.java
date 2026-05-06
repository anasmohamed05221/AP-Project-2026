package com.sb.stayeaseap.repository;

import com.sb.stayeaseap.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserEmailOrderByCreatedAtDesc(String email);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.room.id = :roomId AND b.checkIn < :checkOut AND b.checkOut > :checkIn")
    boolean existsOverlappingBooking(@Param("roomId") Long roomId,
                                     @Param("checkIn") LocalDate checkIn,
                                     @Param("checkOut") LocalDate checkOut);
}
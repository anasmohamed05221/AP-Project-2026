package com.sb.stayeaseap.repository;

import com.sb.stayeaseap.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserEmailOrderByCreatedAtDesc(String email);

    List<Booking> findTop5ByOrderByCreatedAtDesc();

    @Query("SELECT b FROM Booking b WHERE b.user.email = :email AND b.room.hotel.id = :hotelId AND b.checkOut < :today ORDER BY b.checkOut DESC")
    List<Booking> findPastStaysByUserAndHotel(@Param("email") String email,
                                               @Param("hotelId") Long hotelId,
                                               @Param("today") LocalDate today);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.room.id = :roomId AND b.status != 'cancelled' AND b.checkIn < :checkOut AND b.checkOut > :checkIn")
    boolean existsOverlappingBooking(@Param("roomId") Long roomId,
                                     @Param("checkIn") LocalDate checkIn,
                                     @Param("checkOut") LocalDate checkOut);

    @Query("SELECT b FROM Booking b WHERE b.status != 'cancelled' AND b.createdAt >= :from AND b.createdAt < :to")
    List<Booking> findNonCancelledCreatedBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.user.email = :email AND b.checkIn > :today AND b.status != 'cancelled'")
    long countUpcomingByUserEmail(@Param("email") String email, @Param("today") LocalDate today);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.user.email = :email AND b.checkOut < :today AND b.status != 'cancelled'")
    long countPastByUserEmail(@Param("email") String email, @Param("today") LocalDate today);
}

package com.sb.stayeaseap.service;

import com.sb.stayeaseap.model.Booking;
import com.sb.stayeaseap.model.Hotel;
import com.sb.stayeaseap.model.Room;
import com.sb.stayeaseap.model.User;
import com.sb.stayeaseap.repository.BookingRepository;
import com.sb.stayeaseap.repository.HotelRepository;
import com.sb.stayeaseap.repository.ReviewRepository;
import com.sb.stayeaseap.repository.RoomRepository;
import com.sb.stayeaseap.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;

    public AdminService(UserRepository userRepository,
                        HotelRepository hotelRepository,
                        RoomRepository roomRepository,
                        BookingRepository bookingRepository,
                        ReviewRepository reviewRepository) {
        this.userRepository = userRepository;
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
        this.reviewRepository = reviewRepository;
    }

    public long getUserCount() { return userRepository.count(); }
    public long getHotelCount() { return hotelRepository.count(); }
    public long getBookingCount() { return bookingRepository.count(); }
    public long getReviewCount() { return reviewRepository.count(); }

    public List<Booking> getRecentBookings() {
        return bookingRepository.findTop5ByOrderByCreatedAtDesc();
    }

    public List<User> getAllUsers() { return userRepository.findAll(); }
    public List<Hotel> getAllHotels() { return hotelRepository.findAll(); }
    public List<Room> getAllRooms() { return roomRepository.findAll(); }
    public List<Booking> getAllBookings() { return bookingRepository.findAll(); }

    public Hotel getHotelById(Long id) {
        return hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
    }

    public void saveHotel(Hotel hotel) {
        hotelRepository.save(hotel);
    }

    public void deleteHotel(Long id) {
        hotelRepository.deleteById(id);
    }

    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }

    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
    }

    public void saveRoom(Room room) {
        roomRepository.save(room);
    }

    public void cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setStatus("cancelled");
        bookingRepository.save(booking);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}

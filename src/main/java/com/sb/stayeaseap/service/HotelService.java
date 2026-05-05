package com.sb.stayeaseap.service;

import com.sb.stayeaseap.model.Hotel;
import com.sb.stayeaseap.model.Room;
import com.sb.stayeaseap.repository.HotelRepository;
import com.sb.stayeaseap.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;

    public HotelService(HotelRepository hotelRepository, RoomRepository roomRepository) {
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
    }

    public List<Hotel> getFeaturedHotels() {
        return hotelRepository.findTop4ByOrderByStarsDesc();
    }

    public List<Hotel> searchByKeyword(String keyword) {
        return hotelRepository.searchByKeyword(keyword);
    }

    public Hotel getHotelById(Long id) {
        return hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
    }

    public List<Room> getRoomsByHotel(Long hotelId) {
        return roomRepository.findByHotelId(hotelId);
    }

    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
    }

    public BigDecimal getMinPriceForHotel(Long hotelId) {
        return roomRepository.findMinPriceByHotelId(hotelId).orElse(null);
    }
}
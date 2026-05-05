package com.sb.stayeaseap.controller;

import com.sb.stayeaseap.model.Hotel;
import com.sb.stayeaseap.model.Room;
import com.sb.stayeaseap.service.HotelService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping("/hotel/{id}")
    public String hotelDetail(@PathVariable Long id, Model model) {
        Hotel hotel = hotelService.getHotelById(id);
        List<Room> rooms = hotelService.getRoomsByHotel(id);
        model.addAttribute("hotel", hotel);
        model.addAttribute("rooms", rooms);
        model.addAttribute("averageRating", 0);
        model.addAttribute("reviewCount", 0);
        return "hotels/hotel-detail";
    }

    @GetMapping("/room/{id}")
    public String roomDetail(@PathVariable Long id, Model model) {
        Room room = hotelService.getRoomById(id);
        model.addAttribute("room", room);
        model.addAttribute("averageRating", 0);
        return "hotels/room-detail";
    }
}
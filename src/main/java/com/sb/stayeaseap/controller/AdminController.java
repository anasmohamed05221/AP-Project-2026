package com.sb.stayeaseap.controller;

import com.sb.stayeaseap.model.Hotel;
import com.sb.stayeaseap.model.Room;
import com.sb.stayeaseap.service.AdminService;
import com.sb.stayeaseap.service.HotelService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final HotelService hotelService;

    public AdminController(AdminService adminService, HotelService hotelService) {
        this.adminService = adminService;
        this.hotelService = hotelService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("userCount", adminService.getUserCount());
        model.addAttribute("hotelCount", adminService.getHotelCount());
        model.addAttribute("bookingCount", adminService.getBookingCount());
        model.addAttribute("monthlyRevenue", adminService.getMonthlyRevenue());
        model.addAttribute("recentBookings", adminService.getRecentBookings());
        return "admin/dashboard";
    }

    @GetMapping("/hotels")
    public String hotels(Model model) {
        List<Hotel> hotels = adminService.getAllHotels();
        Map<Long, Integer> roomCounts = new HashMap<>();
        for (Hotel hotel : hotels) {
            roomCounts.put(hotel.getId(), hotelService.getRoomsByHotel(hotel.getId()).size());
        }
        model.addAttribute("hotels", hotels);
        model.addAttribute("roomCounts", roomCounts);
        model.addAttribute("rooms", adminService.getAllRooms());
        return "admin/hotels";
    }

    @GetMapping("/hotels/add")
    public String addHotelForm(Model model) {
        model.addAttribute("hotel", new Hotel());
        return "admin/hotel-form";
    }

    @PostMapping("/hotels/add")
    public String addHotel(@RequestParam String name,
                           @RequestParam String city,
                           @RequestParam String location,
                           @RequestParam String description,
                           @RequestParam String image,
                           @RequestParam Integer stars) {
        Hotel hotel = new Hotel();
        hotel.setName(name);
        hotel.setCity(city);
        hotel.setLocation(location);
        hotel.setDescription(description);
        hotel.setImage(image);
        hotel.setStars(stars);
        adminService.saveHotel(hotel);
        return "redirect:/admin/hotels";
    }

    @GetMapping("/hotels/edit/{id}")
    public String editHotelForm(@PathVariable Long id, Model model) {
        model.addAttribute("hotel", adminService.getHotelById(id));
        return "admin/hotel-form";
    }

    @PostMapping("/hotels/edit/{id}")
    public String editHotel(@PathVariable Long id,
                            @RequestParam(required = false) String name,
                            @RequestParam(required = false) String city,
                            @RequestParam(required = false) String location,
                            @RequestParam(required = false) String description,
                            @RequestParam(required = false) String image,
                            @RequestParam(required = false) Integer stars) {
        Hotel hotel = adminService.getHotelById(id);
        if (name != null && !name.isBlank()) hotel.setName(name);
        if (city != null && !city.isBlank()) hotel.setCity(city);
        if (location != null && !location.isBlank()) hotel.setLocation(location);
        if (description != null && !description.isBlank()) hotel.setDescription(description);
        if (image != null && !image.isBlank()) hotel.setImage(image);
        if (stars != null) hotel.setStars(stars);
        adminService.saveHotel(hotel);
        return "redirect:/admin/hotels";
    }

    @PostMapping("/hotels/delete/{id}")
    public String deleteHotel(@PathVariable Long id) {
        adminService.deleteHotel(id);
        return "redirect:/admin/hotels";
    }

    @GetMapping("/hotels/{hotelId}/rooms/add")
    public String addRoomForm(@PathVariable Long hotelId, Model model) {
        model.addAttribute("hotel", adminService.getHotelById(hotelId));
        model.addAttribute("room", new Room());
        return "admin/room-form";
    }

    @PostMapping("/hotels/{hotelId}/rooms/add")
    public String addRoom(@PathVariable Long hotelId,
                          @RequestParam String name,
                          @RequestParam String type,
                          @RequestParam BigDecimal pricePerNight,
                          @RequestParam String description,
                          @RequestParam String image,
                          @RequestParam(defaultValue = "true") Boolean isAvailable) {
        Hotel hotel = adminService.getHotelById(hotelId);
        Room room = new Room();
        room.setHotel(hotel);
        room.setName(name);
        room.setType(type);
        room.setPricePerNight(pricePerNight);
        room.setDescription(description);
        room.setImage(image);
        room.setIsAvailable(isAvailable);
        adminService.saveRoom(room);
        return "redirect:/admin/hotels";
    }

    @GetMapping("/rooms/edit/{id}")
    public String editRoomForm(@PathVariable Long id, Model model) {
        model.addAttribute("room", adminService.getRoomById(id));
        return "admin/room-form";
    }

    @PostMapping("/rooms/edit/{id}")
    public String editRoom(@PathVariable Long id,
                           @RequestParam(required = false) String name,
                           @RequestParam(required = false) String type,
                           @RequestParam(required = false) BigDecimal pricePerNight,
                           @RequestParam(required = false) String description,
                           @RequestParam(required = false) String image,
                           @RequestParam(required = false) Boolean isAvailable) {
        Room room = adminService.getRoomById(id);
        if (name != null && !name.isBlank()) room.setName(name);
        if (type != null && !type.isBlank()) room.setType(type);
        if (pricePerNight != null) room.setPricePerNight(pricePerNight);
        if (description != null && !description.isBlank()) room.setDescription(description);
        if (image != null && !image.isBlank()) room.setImage(image);
        if (isAvailable != null) room.setIsAvailable(isAvailable);
        adminService.saveRoom(room);
        return "redirect:/admin/hotels";
    }

    @PostMapping("/rooms/delete/{id}")
    public String deleteRoom(@PathVariable Long id) {
        adminService.deleteRoom(id);
        return "redirect:/admin/hotels";
    }

    @GetMapping("/bookings")
    public String bookings(Model model) {
        model.addAttribute("bookings", adminService.getAllBookings());
        return "admin/bookings";
    }

    @PostMapping("/bookings/cancel/{id}")
    public String cancelBooking(@PathVariable Long id) {
        adminService.cancelBooking(id);
        return "redirect:/admin/bookings";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", adminService.getAllUsers());
        return "admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return "redirect:/admin/users";
    }
}
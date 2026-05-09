package com.sb.stayeaseap.service;

import com.sb.stayeaseap.model.Hotel;
import com.sb.stayeaseap.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Comparator;

@Service
public class SearchService {

    private final HotelService hotelService;
    private final ReviewRepository reviewRepository;

    public SearchService(HotelService hotelService, ReviewRepository reviewRepository) {
        this.hotelService = hotelService;
        this.reviewRepository = reviewRepository;
    }

    private static final int PER_PAGE = 4;

    public List<Hotel> search(String keyword, List<Integer> stars, Integer maxPrice, String sort, int page) {
        List<Hotel> hotels = hotelService.searchByKeyword(keyword);
        Map<Long, BigDecimal> minPrices = buildMinPrices(hotels);

        hotels = filterByStars(hotels, stars);
        hotels = filterByMaxPrice(hotels, minPrices, maxPrice);
        hotels = sortHotels(hotels, minPrices, sort);

        int offset = (page - 1) * PER_PAGE;
        int end    = Math.min(offset + PER_PAGE, hotels.size());
        if (offset >= hotels.size()) return List.of();
        return hotels.subList(offset, end);
    }

    public int totalPages(String keyword, List<Integer> stars, Integer maxPrice) {
        List<Hotel> hotels = hotelService.searchByKeyword(keyword);
        Map<Long, BigDecimal> minPrices = buildMinPrices(hotels);
        hotels = filterByStars(hotels, stars);
        hotels = filterByMaxPrice(hotels, minPrices, maxPrice);
        return (int) Math.ceil((double) hotels.size() / PER_PAGE);
    }

    public Map<Long, BigDecimal> buildMinPrices(List<Hotel> hotels) {
        Map<Long, BigDecimal> minPrices = new HashMap<>();
        for (Hotel hotel : hotels) {
            minPrices.put(hotel.getId(), hotelService.getMinPriceForHotel(hotel.getId()));
        }
        return minPrices;
    }



    private List<Hotel> filterByStars(List<Hotel> hotels, List<Integer> stars) {
    if (stars == null || stars.isEmpty()) return hotels;

    List<Hotel> result = new ArrayList<>();
    for (Hotel h : hotels) {
        if (stars.contains(h.getStars())) {
            result.add(h);
        }
    }
    return result;
    }

    private List<Hotel> filterByMaxPrice(List<Hotel> hotels, Map<Long, BigDecimal> minPrices, Integer maxPrice) {
    if (maxPrice == null) return hotels;

    List<Hotel> result = new ArrayList<>();
    for (Hotel h : hotels) {
        BigDecimal price = minPrices.get(h.getId());
        if (price == null || price.compareTo(BigDecimal.valueOf(maxPrice)) <= 0) {
            result.add(h);
        }
    }
    return result;
}


    private List<Hotel> sortHotels(List<Hotel> hotels, Map<Long, BigDecimal> minPrices, String sort) {
        if ("price_asc".equals(sort)) {
            hotels.sort(new Comparator<Hotel>() {
                public int compare(Hotel a, Hotel b) {
                    return getPrice(a, minPrices).compareTo(getPrice(b, minPrices));
                }
            });
        } else if ("price_desc".equals(sort)) {
            hotels.sort(new Comparator<Hotel>() {
                public int compare(Hotel a, Hotel b) {
                    return getPrice(b, minPrices).compareTo(getPrice(a, minPrices));
                }
            });
        } else {
            Map<Long, Double> avgRatings = new HashMap<>();
            for (Hotel h : hotels) {
                Double r = reviewRepository.findAvgRatingByHotelId(h.getId());
                avgRatings.put(h.getId(), r != null ? r : 0.0);
            }
            hotels.sort(new Comparator<Hotel>() {
                public int compare(Hotel a, Hotel b) {
                    return Double.compare(avgRatings.get(b.getId()), avgRatings.get(a.getId()));
                }
            });
        }
        return hotels;
    }

    private BigDecimal getPrice(Hotel hotel, Map<Long, BigDecimal> minPrices) {
        return minPrices.getOrDefault(hotel.getId(), BigDecimal.ZERO);
    }

    private int getStars(Hotel hotel) {
        return hotel.getStars() != null ? hotel.getStars() : 0;
    }
}
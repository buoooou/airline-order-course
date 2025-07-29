package com.postion.airlineorderbackend.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class FlightService {

    public List<Map<String, Object>> searchFlights(String departureCity, String arrivalCity, LocalDateTime departureDate) {
        List<Map<String, Object>> flights = new ArrayList<>();
        
        // 模拟航班数据
        String[] flightNumbers = {"CA1234", "MU5678", "CZ9012", "HU3456", "MF7890"};
        String[] airlines = {"中国国际航空", "东方航空", "南方航空", "海南航空", "厦门航空"};
        
        for (int i = 0; i < 5; i++) {
            Map<String, Object> flight = new HashMap<>();
            flight.put("flightNumber", flightNumbers[i]);
            flight.put("airline", airlines[i]);
            flight.put("departureCity", departureCity);
            flight.put("arrivalCity", arrivalCity);
            flight.put("departureTime", departureDate.plusHours(i + 1));
            flight.put("arrivalTime", departureDate.plusHours(i + 3));
            flight.put("price", 800 + (i * 200));
            flight.put("availableSeats", 50 - (i * 5));
            
            flights.add(flight);
        }
        
        return flights;
    }

    public Map<String, Object> getFlightDetails(String flightNumber) {
        Map<String, Object> flight = new HashMap<>();
        flight.put("flightNumber", flightNumber);
        flight.put("airline", "中国国际航空");
        flight.put("departureCity", "北京");
        flight.put("arrivalCity", "上海");
        flight.put("departureTime", LocalDateTime.now().plusDays(1).withHour(10).withMinute(30));
        flight.put("arrivalTime", LocalDateTime.now().plusDays(1).withHour(12).withMinute(30));
        flight.put("price", 1200);
        flight.put("availableSeats", 45);
        flight.put("aircraft", "Boeing 737-800");
        flight.put("status", "ON_TIME");
        
        return flight;
    }

    public boolean checkSeatAvailability(String flightNumber, int seats) {
        // 模拟座位检查
        return Math.random() > 0.1; // 90%概率有座位
    }

    public Map<String, Object> bookFlight(String flightNumber, int seats, String passengerName) {
        Map<String, Object> booking = new HashMap<>();
        
        if (checkSeatAvailability(flightNumber, seats)) {
            booking.put("success", true);
            booking.put("bookingReference", "BK" + System.currentTimeMillis());
            booking.put("flightNumber", flightNumber);
            booking.put("seats", seats);
            booking.put("passengerName", passengerName);
            booking.put("status", "CONFIRMED");
        } else {
            booking.put("success", false);
            booking.put("message", "No available seats");
        }
        
        return booking;
    }
} 
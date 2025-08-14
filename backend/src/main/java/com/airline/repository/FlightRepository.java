package com.airline.repository;

import com.airline.entity.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    Optional<Flight> findByFlightNumber(String flightNumber);

    @Query("SELECT f FROM Flight f WHERE f.departureAirport.id = :departureAirportId " +
           "AND f.arrivalAirport.id = :arrivalAirportId " +
           "AND DATE(f.departureTime) = DATE(:departureDate) " +
           "AND f.status IN ('SCHEDULED', 'BOARDING', 'DELAYED') " +
           "AND f.availableSeats > 0")
    Page<Flight> searchFlights(@Param("departureAirportId") Long departureAirportId,
                              @Param("arrivalAirportId") Long arrivalAirportId,
                              @Param("departureDate") LocalDateTime departureDate,
                              Pageable pageable);

    @Query("SELECT f FROM Flight f WHERE f.airline.id = :airlineId")
    Page<Flight> findByAirlineId(@Param("airlineId") Long airlineId, Pageable pageable);

    @Query("SELECT f FROM Flight f WHERE f.status = :status")
    Page<Flight> findByStatus(@Param("status") Flight.Status status, Pageable pageable);

    @Query("SELECT f FROM Flight f WHERE f.departureTime BETWEEN :startTime AND :endTime")
    List<Flight> findByDepartureTimeBetween(@Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime);

    @Query("SELECT f FROM Flight f WHERE " +
           "(LOWER(f.flightNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(f.airline.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(f.departureAirport.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(f.arrivalAirport.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Flight> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT COUNT(f) FROM Flight f WHERE f.status = :status")
    long countByStatus(@Param("status") Flight.Status status);

    @Query("SELECT f FROM Flight f WHERE f.status IN ('SCHEDULED', 'BOARDING') " +
           "AND f.departureTime < :cutoffTime")
    List<Flight> findFlightsToUpdateStatus(@Param("cutoffTime") LocalDateTime cutoffTime);

    @Query("SELECT f FROM Flight f WHERE f.departureAirport.code = :departureCode " +
           "AND f.arrivalAirport.code = :arrivalCode " +
           "AND f.departureTime BETWEEN :startDate AND :endDate " +
           "AND f.status IN ('SCHEDULED', 'BOARDING', 'DELAYED') " +
           "ORDER BY f.departureTime")
    List<Flight> findAvailableFlights(@Param("departureCode") String departureCode,
                                     @Param("arrivalCode") String arrivalCode,
                                     @Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);
}
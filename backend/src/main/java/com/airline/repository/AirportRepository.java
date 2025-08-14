package com.airline.repository;

import com.airline.entity.Airport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AirportRepository extends JpaRepository<Airport, Long> {

    Optional<Airport> findByCode(String code);

    boolean existsByCode(String code);

    List<Airport> findByStatus(Airport.Status status);

    List<Airport> findByCountryAndStatus(String country, Airport.Status status);

    List<Airport> findByCityAndStatus(String city, Airport.Status status);

    @Query("SELECT a FROM Airport a WHERE a.status = :status")
    Page<Airport> findByStatus(@Param("status") Airport.Status status, Pageable pageable);

    @Query("SELECT a FROM Airport a WHERE " +
           "(LOWER(a.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.city) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.country) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Airport> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT DISTINCT a.country FROM Airport a WHERE a.status = :status ORDER BY a.country")
    List<String> findDistinctCountriesByStatus(@Param("status") Airport.Status status);

    @Query("SELECT DISTINCT a.city FROM Airport a WHERE a.country = :country AND a.status = :status ORDER BY a.city")
    List<String> findDistinctCitiesByCountryAndStatus(@Param("country") String country, @Param("status") Airport.Status status);
}
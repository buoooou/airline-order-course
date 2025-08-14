package com.airline.repository;

import com.airline.entity.Airline;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AirlineRepository extends JpaRepository<Airline, Long> {

    Optional<Airline> findByCode(String code);

    boolean existsByCode(String code);

    List<Airline> findByStatus(Airline.Status status);

    @Query("SELECT a FROM Airline a WHERE a.status = :status")
    Page<Airline> findByStatus(@Param("status") Airline.Status status, Pageable pageable);

    @Query("SELECT a FROM Airline a WHERE " +
           "(LOWER(a.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.country) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Airline> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT COUNT(a) FROM Airline a WHERE a.status = :status")
    long countByStatus(@Param("status") Airline.Status status);
}
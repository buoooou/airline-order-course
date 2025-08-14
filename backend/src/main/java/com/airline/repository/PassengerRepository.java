package com.airline.repository;

import com.airline.entity.Passenger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {

    @Query("SELECT p FROM Passenger p WHERE p.user.id = :userId")
    List<Passenger> findByUserId(@Param("userId") Long userId);

    Optional<Passenger> findByPassportNumber(String passportNumber);

    Optional<Passenger> findByIdCardNumber(String idCardNumber);

    @Query("SELECT p FROM Passenger p WHERE " +
           "(LOWER(p.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.passportNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.idCardNumber) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Passenger> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    boolean existsByPassportNumber(String passportNumber);

    boolean existsByIdCardNumber(String idCardNumber);
}
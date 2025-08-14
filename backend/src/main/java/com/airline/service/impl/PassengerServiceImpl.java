package com.airline.service.impl;

import com.airline.dto.PassengerDto;
import com.airline.entity.Passenger;
import com.airline.entity.User;
import com.airline.exception.ResourceNotFoundException;
import com.airline.exception.ValidationException;
import com.airline.mapper.PassengerMapper;
import com.airline.repository.PassengerRepository;
import com.airline.repository.UserRepository;
import com.airline.service.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;
    private final UserRepository userRepository;
    private final PassengerMapper passengerMapper;

    @Autowired
    public PassengerServiceImpl(PassengerRepository passengerRepository,
                               UserRepository userRepository,
                               PassengerMapper passengerMapper) {
        this.passengerRepository = passengerRepository;
        this.userRepository = userRepository;
        this.passengerMapper = passengerMapper;
    }

    @Override
    public PassengerDto createPassenger(PassengerDto passengerDto, Long userId) {
        validatePassengerDto(passengerDto);
        
        Passenger passenger = passengerMapper.toEntity(passengerDto);
        
        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
            passenger.setUser(user);
        }
        
        Passenger savedPassenger = passengerRepository.save(passenger);
        return passengerMapper.toDto(savedPassenger);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PassengerDto> getPassengerById(Long id) {
        return passengerRepository.findById(id)
                .map(passengerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PassengerDto> getPassengersByUser(Long userId) {
        return passengerMapper.toDtoList(
                passengerRepository.findByUserId(userId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PassengerDto> getAllPassengers(Pageable pageable) {
        return passengerRepository.findAll(pageable)
                .map(passengerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PassengerDto> searchPassengers(String keyword, Pageable pageable) {
        return passengerRepository.findByKeyword(keyword, pageable)
                .map(passengerMapper::toDto);
    }

    @Override
    public PassengerDto updatePassenger(Long id, PassengerDto passengerDto) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("旅客不存在"));
        
        validatePassengerDto(passengerDto, id);
        
        passengerMapper.updateEntityFromDto(passengerDto, passenger);
        Passenger savedPassenger = passengerRepository.save(passenger);
        return passengerMapper.toDto(savedPassenger);
    }

    @Override
    public void deletePassenger(Long id) {
        if (!passengerRepository.existsById(id)) {
            throw new ResourceNotFoundException("旅客不存在");
        }
        passengerRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PassengerDto> getPassengerByPassport(String passportNumber) {
        return passengerRepository.findByPassportNumber(passportNumber)
                .map(passengerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PassengerDto> getPassengerByIdCard(String idCardNumber) {
        return passengerRepository.findByIdCardNumber(idCardNumber)
                .map(passengerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByPassportNumber(String passportNumber) {
        return passengerRepository.existsByPassportNumber(passportNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIdCardNumber(String idCardNumber) {
        return passengerRepository.existsByIdCardNumber(idCardNumber);
    }

    private void validatePassengerDto(PassengerDto passengerDto) {
        validatePassengerDto(passengerDto, null);
    }

    private void validatePassengerDto(PassengerDto passengerDto, Long excludeId) {
        if (passengerDto.getPassportNumber() != null && !passengerDto.getPassportNumber().isEmpty()) {
            Optional<Passenger> existingByPassport = passengerRepository.findByPassportNumber(passengerDto.getPassportNumber());
            if (existingByPassport.isPresent() && 
                (excludeId == null || !existingByPassport.get().getId().equals(excludeId))) {
                throw new ValidationException("护照号已存在");
            }
        }
        
        if (passengerDto.getIdCardNumber() != null && !passengerDto.getIdCardNumber().isEmpty()) {
            Optional<Passenger> existingByIdCard = passengerRepository.findByIdCardNumber(passengerDto.getIdCardNumber());
            if (existingByIdCard.isPresent() && 
                (excludeId == null || !existingByIdCard.get().getId().equals(excludeId))) {
                throw new ValidationException("身份证号已存在");
            }
        }
    }
}
package com.position.airlineorderbackend.repo;

import com.position.airlineorderbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
} 
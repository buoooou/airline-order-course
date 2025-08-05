package com.postion.airlineorderbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.postion.airlineorderbackend.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}

package com.postion.airlineorderbackend.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.postion.airlineorderbackend.entity.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

}

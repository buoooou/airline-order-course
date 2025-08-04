package com.postion.airlineorderbackend.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.postion.airlineorderbackend.entity.AppUser;

// 继承 JpaRepository，Spring 会自动生成实现类并注入容器
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

}

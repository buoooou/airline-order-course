package com.postion.airlineorderbackend.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity
@Table(name = "app_users") // 对应数据库表名
@Data // 使用Lombok简化getter、setter等方法
public class AppUser {

    @Id // 标记为主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增策略，对应AUTO_INCREMENT
    private Long id;

    @Column(name = "username", length = 250, nullable = false, unique = true)
    private String username;

    @Column(name = "password", length = 250, nullable = false)
    @JsonIgnore
    private String password;

    @Column(name = "role", length = 50, nullable = false)
    private String role;

    // 一对多关联订单表
    // @OneToMany(mappedBy = "userId", fetch = FetchType.LAZY, cascade =
    // CascadeType.ALL)
    @OneToMany(mappedBy = "userId", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Order> orders = new ArrayList<>();

}

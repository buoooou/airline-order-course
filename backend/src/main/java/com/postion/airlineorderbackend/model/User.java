package com.postion.airlineorderbackend.model;

import lombok.Data;

import jakarta.persistence.*;

@Entity
@Table(name = "app_users_wxl")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String role;
}

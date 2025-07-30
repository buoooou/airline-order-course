package com.postion.airlineorderbackend.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "app_users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    private String role;
}

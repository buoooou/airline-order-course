package com.postion.airlineorderbackend.model;

import lombok.Data;
import javax.persistence.*;

@Entity
@Table(name = "app_users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    private String role;
}


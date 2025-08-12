package com.postion.airlineorderbackend.model;

import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "app_users_ycr")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String role;
}

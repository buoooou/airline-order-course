package com.postion.airlineorderbackend.model;
import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "app_users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String role;
}

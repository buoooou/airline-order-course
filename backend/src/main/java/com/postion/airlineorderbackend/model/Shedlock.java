package com.postion.airlineorderbackend.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "shedlock")
public class Shedlock {
    // name var64
    // lock_until timestamp
    // locked_at
    // locked_by
    // primary name

    @Id
    @Column(name = "name", nullable = false, unique=true, length = 64)
    private String name;

    @Column(name = "lock_until", nullable = false)
    private Timestamp lock_until;

    @Column(name = "locked_at", nullable = false)
    private Timestamp locked_at;

    @Column(name = "locked_by", nullable = false)
    private Timestamp locked_by;



}

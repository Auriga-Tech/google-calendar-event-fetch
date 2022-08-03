package com.aurigait.googleCalendar.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 80, nullable = true)
    private String name;

    @Column(length = 80, unique = true)
    private String email;

    //	@Column(length=50)
    private String refreshToken;

    @Column(unique = true, nullable = false)
    private String accessToken;

    private String photo;
}

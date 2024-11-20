package com.example._2024_danpoong_team_39_be.domain;


import jakarta.persistence.*;
import lombok.*;

// Member domain

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String name;

    @Column(length = 50)
    private String alias;

    private int age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false, unique = true)
    private int phone_num;

    @Column(nullable = false, length = 50)
    private String email;

    public Member(String name, int age, Gender gender, int phone_num) {
        this.name = name;
        this.alias = alias;
        this.age = age;
        this.gender = gender;
        this.phone_num = phone_num;
        this.email = email;
    }



}
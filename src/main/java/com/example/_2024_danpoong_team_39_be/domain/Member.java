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

    @Column(nullable = false, length = 50)
    private String email;

    @OneToOne(cascade = CascadeType.ALL)
    private upLoadProfile profileImage;

    public Member(String name, String alias, int age, Gender gender, int phone_num, String email, upLoadProfile profileImage) {
        this.name = name;
        this.alias = alias;
        this.age = age;
        this.gender = gender;
        this.email = email;
    }



}
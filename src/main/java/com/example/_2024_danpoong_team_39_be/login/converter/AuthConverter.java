package com.example._2024_danpoong_team_39_be.login.converter;


import com.example._2024_danpoong_team_39_be.domain.Member;

public class AuthConverter {

    // member 객체 생성 (다만 다른 부가정보는 새롭게 입력해야 함)
    public static Member toMember(String email, String alias) {
        return Member.builder()
                .alias(alias)
                .email(email)
                .build();
    }
}

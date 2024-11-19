package com.example._2024_danpoong_team_39_be.login.dto;


import lombok.Getter;
import lombok.Setter;

// 추후 사용 예정
@Getter
@Setter
public class MemberRequestDTO {

    @Getter
    @Setter
    public static class LoginRequestDTO {
        private String email;
        private String password;
        private String alias;
    }
}


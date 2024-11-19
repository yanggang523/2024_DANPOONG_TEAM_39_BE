package com.example._2024_danpoong_team_39_be.login;


public class AuthHandler extends RuntimeException {
    public AuthHandler(ErrorStatus errorStatus) {
        super(errorStatus.getMessage());
    }
}

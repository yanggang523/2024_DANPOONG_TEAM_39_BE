package com.example._2024_danpoong_team_39_be.login;


import lombok.Getter;

// 에러 코드
@Getter
public enum ErrorStatus {
    _PARSING_ERROR("Error occurred while parsing the response");

    private final String message;

    ErrorStatus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}


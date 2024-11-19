package com.example._2024_danpoong_team_39_be.login.dto;



import lombok.Getter;

// 카카오 측에 요청하는 DTO
public class KakaoDTO {

    //토큰 요청용
    @Getter
    public static class OAuthToken {
        private String access_token;
        private String token_type;
        private String refresh_token;
        private int expires_in;
        private String scope;
        private int refresh_token_expires_in;
    }
}

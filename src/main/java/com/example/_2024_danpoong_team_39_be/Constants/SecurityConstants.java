package com.example._2024_danpoong_team_39_be.Constants;

public class SecurityConstants {

    // 허용 url 넣는 부분
    public static final String[] ALLOW_URLS = {
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/api/v1/posts/**",
            "/api/v1/replies/**",
            "/login",
            "/auth/login/kakao/**",
            "/api/member/signup"
    };

}

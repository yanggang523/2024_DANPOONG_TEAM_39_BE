package com.example._2024_danpoong_team_39_be.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;
import org.springframework.web.cors.CorsConfigurationSource;
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 허용할 도메인 추가 (명확한 도메인 또는 패턴 사용)
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:*",        // 모든 로컬 포트 허용
                "http://34.236.139.89:*"    // 특정 도메인의 모든 포트 허용
        ));

        // 허용할 HTTP 메서드
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 허용할 요청 헤더
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        // 응답 헤더에 추가
        config.setExposedHeaders(List.of("Authorization", "Content-Type"));

        // 인증 정보 포함 허용
        config.setAllowCredentials(true);

        // 모든 경로에 대해 설정 적용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}

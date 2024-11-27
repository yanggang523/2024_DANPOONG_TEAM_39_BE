package com.example._2024_danpoong_team_39_be.login.util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
@Component
public class JwtUtil {
    // 키 생성
    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 액세스 토큰 만료 시간 (1시간)
    private final long accessExpirationTime = 1000 * 60 * 60;

    // 리프레시 토큰 만료 시간 (7일)
    private final long refreshExpirationTime = 1000 * 60 * 60 * 24 * 7;
    // JWT 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);  // 서명 검증
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    // JWT 토큰에서 사용자 인증 정보를 가져옴
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        String username = claims.getSubject();

        // 여기서 UserDetailsService를 이용해 사용자 정보를 조회하거나, 직접 사용자 객체를 생성할 수 있습니다.
        return new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
    }


    // 토큰에서 만료 시간 가져오기
    public Date getExpirationDateFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        return claims.getExpiration();
    }


    // JWT 토큰 생성
    public String createAccessToken(String email) {


        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExpirationTime))
                .signWith(secretKey)
                .compact();
    }

    // 리프레시 토큰 생성
    public String createRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationTime))
                .signWith(secretKey)
                .compact();
    }


    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject(); // JWT의 subject 필드에서 이메일 반환
    }

}

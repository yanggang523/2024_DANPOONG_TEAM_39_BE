package com.example._2024_danpoong_team_39_be.invitation.util;

import com.example._2024_danpoong_team_39_be.invitation.domain.InvitationCode;
import com.example._2024_danpoong_team_39_be.invitation.repository.InvitationCodeRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class InvitationUtil {

    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public static String generateToken(Long careRecipientId, Long careAssignmentId, long expirationTime) {
        return Jwts.builder()
                .setSubject("GroupInvitation")
                .claim("careRecipientId", careRecipientId)
                .claim("careAssignmentId", careAssignmentId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SECRET_KEY)
                .compact();
    }

    public static String generateShortCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 6);
    }

    public static void validateToken(String token) {
        Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token);
    }

    public static Key getSecretKey() {
        return SECRET_KEY;
    }
}

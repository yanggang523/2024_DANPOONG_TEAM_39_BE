//package com.example._2024_danpoong_team_39_be;
//
//import com.example._2024_danpoong_team_39_be.login.util.JwtUtil;
//import io.jsonwebtoken.Jwt;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//public class JWTAuthenticationFilter extends OncePerRequestFilter {
//    private final JwtUtil jwtService;  // JWT 서비스
//
//    public JWTAuthenticationFilter(JwtUtil jwtUtil) {
//        this.jwtService = jwtUtil;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String token = getTokenFromRequest(request);  // 요청에서 JWT 토큰 추출
//
//        if (token != null && jwtService.validateToken(token)) {
//            Authentication authentication = jwtService.getAuthentication(token);  // 인증 정보 가져오기
//            SecurityContextHolder.getContext().setAuthentication(authentication);  // 인증 설정
//        }
//
//        filterChain.doFilter(request, response);  // 다음 필터로 요청 전달
//    }
//
//    private String getTokenFromRequest(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7);  // "Bearer " 제외한 토큰만 반환
//        }
//        return null;
//    }
//
//}

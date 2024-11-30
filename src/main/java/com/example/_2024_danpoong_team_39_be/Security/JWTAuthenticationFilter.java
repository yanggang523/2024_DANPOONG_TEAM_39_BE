package com.example._2024_danpoong_team_39_be.Security;


import com.example._2024_danpoong_team_39_be.domain.Member;
import com.example._2024_danpoong_team_39_be.login.util.JwtUtil;
import com.example._2024_danpoong_team_39_be.login.repository.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    public JWTAuthenticationFilter(JwtUtil jwtUtil, MemberRepository memberRepository) {
        this.jwtUtil = jwtUtil;
        this.memberRepository = memberRepository;
    }

    /**
     * 특정 경로에서 필터를 적용하지 않도록 설정
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // 공개 경로에서 필터 제외
        return path.startsWith("/api/member/signup") ||
                path.startsWith("/auth/login") ||
                path.startsWith("/favicon.ico") ||
                path.startsWith("/swagger-ui/") ||
                path.startsWith("/v3/api-docs/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            String authorizationHeader = request.getHeader("Authorization");
            log.info("Authorization 헤더 값: {}", authorizationHeader);

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7).trim(); // Bearer 이후 토큰만 추출
                log.info("JWT Token: {}", token);

                jwtUtil.debugJwtToken(token);

                // JWT 검증 및 사용자 인증 처리
                authenticateRequest(token, request);
            }
        } catch (Exception e) {
            log.warn("JWT 토큰 인증 실패: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        // 필터 체인 실행
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("필터 체인 실행 중 예외 발생: {}", e.getMessage());
            throw new RuntimeException("필터 체인 처리 중 오류 발생", e);
        }
    }

    /**
     * JWT 검증 및 인증 처리
     */
    private void authenticateRequest(String token, HttpServletRequest request) {
        try {
            // JWT에서 이메일 추출
            String email = jwtUtil.getEmailFromToken(token);
            log.debug("JWT에서 추출된 이메일: {}", email);

            // 이메일로 사용자 조회
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Member not found with email: " + email));

            // Spring Security 인증 객체 생성 및 설정
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(member, null, null);
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (Exception e) {
            log.error("사용자 인증 실패: {}", e.getMessage());
            throw e; // 예외를 상위로 전달
        }
    }
}

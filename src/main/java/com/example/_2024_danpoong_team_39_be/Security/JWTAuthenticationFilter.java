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


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        String authorizationHeader = request.getHeader("Authorization");
        log.info("Authorization 헤더 값: {}", authorizationHeader);



        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            log.info("베리어 토큰으로 인식된 상황");
            String token = authorizationHeader.substring(7).trim(); // Bearer 이후 토큰만 추출
            log.info("token:{}", token);

            try {
                // JWT에서 이메일 추출
                String email = jwtUtil.getEmailFromToken(token);

                log.info("시큐리티 jwt email 조회 : " + email);

                // 이메일로 Member 조회
                Member member = memberRepository.findByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException("Member not found with email: " + email));

                // 인증 정보 설정
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(member, null, null);
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } catch (Exception e) {
                log.warn("JWT 토큰 인증 실패 " + e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }
        // 필터 체인 실행 및 예외 처리 추가
        try {
            log.info("request:{}", request);
            log.info("response:{}", response);
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("필터 체인 실행 중 예외 발생: {}", e.getMessage());
        }
        //filterChain.doFilter(request, response);
    }
}


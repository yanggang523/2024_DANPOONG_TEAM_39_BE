package com.example._2024_danpoong_team_39_be.login;


import com.example._2024_danpoong_team_39_be.login.domain.Member;
import com.example._2024_danpoong_team_39_be.login.dto.KakaoDTO;
import com.example._2024_danpoong_team_39_be.login.util.KakaoUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final KakaoUtil kakaoUtil;



    //@Override
    public Member oAuthLogin(String accessCode, HttpServletResponse httpServletResponse) {

        //카카오 서버에 토큰 요청
        log.info("카카오 토큰 요청 시작");
        KakaoDTO.OAuthToken oAuthToken = kakaoUtil.requestToken(accessCode);
        log.info("카카오 토큰 요청 끝");
        return null;
    }
}


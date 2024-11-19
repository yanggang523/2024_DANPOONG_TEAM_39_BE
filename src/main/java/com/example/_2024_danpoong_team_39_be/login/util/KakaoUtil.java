package com.example._2024_danpoong_team_39_be.login.util;


import com.example._2024_danpoong_team_39_be.login.AuthHandler;
import com.example._2024_danpoong_team_39_be.login.ErrorStatus;
import com.example._2024_danpoong_team_39_be.login.dto.KakaoDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Component
@Slf4j
public class KakaoUtil {

    // REST_API_KEY와 REDIRECT_URL (프로퍼티에 기재)
    @Value("${kakao.auth.client}")
    private String client;
    @Value("${kakao.auth.redirect}")
    private String redirect;

    // 토큰 요청 부분
    public KakaoDTO.OAuthToken requestToken(String accessCode) {
        log.info("requestToken 메서드가 호출되었습니다. Access Code: " + accessCode);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // 보낼 body에 요청 내용 추가
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", client);
        params.add("redirect_url", redirect);
        params.add("code", accessCode);

        // header body 묶기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);
        log.info("카카오 토큰 요청을 전송합니다.");

        // Kakao 서버에 유저 토큰 요청
        ResponseEntity<String> response = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class);

        log.info("카카오 토큰 요청에 대한 응답을 받았습니다. 응답 내용: " + response.getBody());


        // ObjectMapper로 보낼 객체 매핑
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoDTO.OAuthToken oAuthToken;


        try {
            oAuthToken = objectMapper.readValue(response.getBody(), KakaoDTO.OAuthToken.class);
            log.info("oAuthToken : " + oAuthToken.getAccess_token());
        } catch (JsonProcessingException e) {
            throw new AuthHandler(ErrorStatus._PARSING_ERROR);
        }
        return oAuthToken;
    }
}
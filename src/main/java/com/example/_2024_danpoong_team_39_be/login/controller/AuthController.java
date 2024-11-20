package com.example._2024_danpoong_team_39_be.login.controller;



import com.example._2024_danpoong_team_39_be.login.AuthService;
import com.example._2024_danpoong_team_39_be.login.BaseResponse;
import com.example._2024_danpoong_team_39_be.login.converter.MemberConverter;
import com.example._2024_danpoong_team_39_be.domain.Member;
import com.example._2024_danpoong_team_39_be.login.dto.MemberRequestDTO;
import com.example._2024_danpoong_team_39_be.login.dto.MemberResponseDTO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class AuthController {

    private final AuthService authService;


    @PostMapping("/login") //url 추후 수정 예정
    public ResponseEntity<?> join(@RequestBody MemberRequestDTO.LoginRequestDTO loginRequestDTO) {
        return null; // 추후 jwt 로직 추가 예정(토큰으로 프론트에 보내줌)
    }


    // 카카오 로그인에 토큰 요청


    @GetMapping("/auth/login/kakao")
    public BaseResponse<MemberResponseDTO.JoinResultDTO> kakaoLogin(@RequestParam("code") String accessCode, HttpServletResponse httpServletResponse) {
        Member member = authService.oAuthLogin(accessCode, httpServletResponse);
        return BaseResponse.onSuccess(MemberConverter.toJoinResultDTO(member));
    }
}

package com.example._2024_danpoong_team_39_be.login.controller;



import com.example._2024_danpoong_team_39_be.domain.CareRecipient;
import com.example._2024_danpoong_team_39_be.login.converter.CareRecipientConverter;
import com.example._2024_danpoong_team_39_be.login.dto.CareRecipientDTO;
import com.example._2024_danpoong_team_39_be.login.dto.FillupResultDTO;
import com.example._2024_danpoong_team_39_be.login.service.AuthService;
import com.example._2024_danpoong_team_39_be.login.BaseResponse;
import com.example._2024_danpoong_team_39_be.login.converter.MemberConverter;
import com.example._2024_danpoong_team_39_be.domain.Member;
import com.example._2024_danpoong_team_39_be.login.dto.MemberRequestDTO;
import com.example._2024_danpoong_team_39_be.login.dto.MemberResponseDTO;
import com.example._2024_danpoong_team_39_be.login.service.CareRecipentService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class AuthController {

    private final AuthService authService;

    // 멤버 추가 정보 입력
    @PutMapping("api/member/signup/fillup")
    public ResponseEntity<FillupResultDTO> updateAdditionalInfo(
            @RequestHeader("Authorization") String token,
            @RequestBody MemberRequestDTO.UpdateInfoRequestDTO updateInfo) {
        String jwtToken = token.replace("Bearer ", "");
        Member updatedMember = authService.updateAdditionalInfo(jwtToken, updateInfo);
        return ResponseEntity.ok(FillupResultDTO.of(updatedMember));
    }


    // 카카오 로그인 (토큰 요청 후 로그인)
    @GetMapping("/api/member/signup")
    public BaseResponse<MemberResponseDTO.JoinResultDTO> kakaoLogin(@RequestParam("code") String accessCode, HttpServletResponse httpServletResponse) {
        Member member = authService.oAuthLogin(accessCode, httpServletResponse);
        return BaseResponse.onSuccess(MemberConverter.toJoinResultDTO(member));
    }

    // 최초 돌봄가족 입력 (토큰 요청 후 로그인)
    @PostMapping("/api/care_recipient/{member_id}")
    public BaseResponse<CareRecipientDTO> CareRecipientFillup(){
        CareRecipient careRecipient = CareRecipentService.createCareRecipient;
        return BaseResponse.onSuccess(CareRecipientConverter.toCareRecipient(newCareRecipient));
    }
}

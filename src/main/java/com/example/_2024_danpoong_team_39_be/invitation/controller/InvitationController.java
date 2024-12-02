package com.example._2024_danpoong_team_39_be.invitation.controller;


import com.example._2024_danpoong_team_39_be.domain.Member;
import com.example._2024_danpoong_team_39_be.invitation.InvitationDTO;
import com.example._2024_danpoong_team_39_be.invitation.service.InvitationService;
import com.example._2024_danpoong_team_39_be.login.repository.MemberRepository;
import com.example._2024_danpoong_team_39_be.login.util.JwtUtil;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example._2024_danpoong_team_39_be.invitation.util.InvitationUtil;

import java.util.Map;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class InvitationController {

    private final InvitationService invitationService;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    @PostMapping("/api/invitation")
    public ResponseEntity<Map<String, String>> createInvitation(@RequestBody InvitationDTO.InvitationRequest request) {
        try {
            Long recipientId = request.getRecipientId();
            Long assignmentId = request.getAssignmentId();

            // 초대 코드 생성
            String invitationCode = invitationService.generateInvitationCode(recipientId, assignmentId);

            // JSON 응답 생성
            Map<String, String> response = Map.of("invitationCode", invitationCode);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("createInvitation - 입력 검증 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("createInvitation - 처리 실패: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", "초대 코드 생성 중 서버 오류가 발생했습니다."));
        }
    }


    @PostMapping("/api/join")
    public ResponseEntity<?> joinGroup(@RequestHeader("Authorization") String token,
                                       @RequestBody Map<String, String> requestBody) {
        try {
            // 1. 요청 Body에서 초대 코드 추출
            String inviteCode = requestBody.get("invitationCode");
            if (inviteCode == null || inviteCode.isBlank()) {
                log.error("invitationCode가 요청에 포함되지 않았습니다.");
                return ResponseEntity.badRequest().body(Map.of("error", "invitationCode가 요청에 포함되지 않았습니다."));
            }
            log.info("받은 invitationCode: {}", inviteCode);

            // 2. 토큰에서 이메일 추출
            String email = jwtUtil.getEmailFromToken(token);
            log.info("추출된 이메일: {}", email);

            // 3. 이메일로 Member 조회
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("해당 이메일로 등록된 회원을 찾을 수 없습니다: " + email));
            log.info("조회된 Member: {}", member);

            // 4. 초대 코드 검증 및 careRecipientId 추출
            String validatedToken = invitationService.validateCode(inviteCode);
            Long careRecipientId = Jwts.parserBuilder()
                    .setSigningKey(InvitationUtil.getSecretKey())
                    .build()
                    .parseClaimsJws(validatedToken)
                    .getBody()
                    .get("careRecipientId", Long.class);
            log.info("초대 코드에서 추출된 careRecipientId: {}", careRecipientId);

            // 5. 그룹에 멤버 추가 및 결과 반환
            InvitationDTO result = invitationService.addMemberToGroup(member.getId(), careRecipientId);
            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("joinGroup - 입력 검증 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("joinGroup - 처리 실패: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", "그룹 가입 처리 중 서버 오류가 발생했습니다."));
        }
    }




    @RestController
    @RequestMapping("/test")
    public class TokenController {

        private final MemberRepository memberRepository;
        private final JwtUtil jwtUtil;

        public TokenController(MemberRepository memberRepository, JwtUtil jwtUtil) {
            this.memberRepository = memberRepository;
            this.jwtUtil = jwtUtil;
        }

        @GetMapping("/token/email")
        public ResponseEntity<String> createAccessToken(@RequestParam Long id) {
            // Member 조회
            Member member = memberRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("해당 ID의 멤버를 찾을 수 없습니다: " + id));

            // 이메일 기반으로 JWT 토큰 생성
            String token = jwtUtil.createAccessToken(member.getEmail());
            log.info("요청 member의 id:{}, token:{}", id, token);

            // 생성된 토큰 반환
            return ResponseEntity.ok(token);
        }
    }


}

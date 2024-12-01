package com.example._2024_danpoong_team_39_be.invitation.controller;


import com.example._2024_danpoong_team_39_be.invitation.service.InvitationService;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example._2024_danpoong_team_39_be.invitation.util.InvitationUtil;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class InvitationController {

    private final InvitationService invitationService;

    @PostMapping("/{groupId}/invite")
    public ResponseEntity<String> createInvitation(@PathVariable Long groupId, @RequestParam Long userId) {
        String invitationCode = invitationService.generateInvitationCode(groupId, userId);
        return ResponseEntity.ok(invitationCode);
    }

    @PostMapping("/join")
    public ResponseEntity<String> joinGroup(@RequestParam String code, @RequestParam Long userId) {
        try {
            String token = invitationService.validateCode(code);
            Long groupId = Jwts.parserBuilder()
                    .setSigningKey(InvitationUtil.getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("careRecipientId", Long.class);

            boolean success = invitationService.addUserToGroup(userId, groupId);
            return success ? ResponseEntity.ok("가입 완료") : ResponseEntity.badRequest().body("가입 실패");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("유효하지 않은 초대 코드입니다.");
        }
    }
}

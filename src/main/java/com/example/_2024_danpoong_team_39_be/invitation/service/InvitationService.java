package com.example._2024_danpoong_team_39_be.invitation.service;

import com.example._2024_danpoong_team_39_be.domain.CareAssignment;
import com.example._2024_danpoong_team_39_be.domain.CareRecipient;
import com.example._2024_danpoong_team_39_be.domain.Member;
import com.example._2024_danpoong_team_39_be.invitation.InvitationDTO;
import com.example._2024_danpoong_team_39_be.invitation.domain.InvitationCode;
import com.example._2024_danpoong_team_39_be.invitation.repository.InvitationCodeRepository;
import com.example._2024_danpoong_team_39_be.invitation.util.InvitationUtil;
import com.example._2024_danpoong_team_39_be.login.repository.CareRecipientRepository;
import com.example._2024_danpoong_team_39_be.careAssignment.CareAssignmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example._2024_danpoong_team_39_be.login.repository.MemberRepository;

@Slf4j
@Service
public class InvitationService {

    private static final long EXPIRATION_TIME = 3600000; // 1시간

    @Autowired
    private CareRecipientRepository careRecipientRepository;

    @Autowired
    private CareAssignmentRepository careAssignmentRepository;

    @Autowired
    private InvitationCodeRepository invitationCodeRepository;

    @Autowired
    private MemberRepository memberRepository;

    public String generateInvitationCode(Long careAssignmentId, Long careRecipientId) {
        String inviteCode = InvitationUtil.generateShortCode();
        String token = InvitationUtil.generateToken(careRecipientId, careAssignmentId, EXPIRATION_TIME);

        CareAssignment careAssignment = careAssignmentRepository.findById(careAssignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid CareAssignment ID: " + careAssignmentId));
        CareRecipient careRecipient = careRecipientRepository.findById(careRecipientId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid CareRecipient ID: " + careRecipientId));

        InvitationCode invitationCode = new InvitationCode(inviteCode, token, careAssignment, careRecipient);
        invitationCodeRepository.save(invitationCode);

        return inviteCode;
    }

    public String validateCode(String code) {
        // 1. 입력값 확인
        if (code == null || code.trim().isEmpty()) {
            log.error("초대 코드가 비어 있습니다.");
            throw new IllegalArgumentException("초대 코드가 비어 있습니다.");
        }

        log.info("초대 코드 검증 시작: {}", code);

        // 2. 초대 코드 조회
        InvitationCode invitation = invitationCodeRepository.findByInviteCode(code)
                .orElseThrow(() -> {
                    log.error("유효하지 않은 초대 코드: {}", code);
                    return new IllegalArgumentException("유효하지 않은 초대 코드입니다.");
                });

        log.info("유효한 초대 코드 확인. 초대 코드: {}, 토큰: {}", invitation.getInviteCode(), invitation.getToken());

        // 3. JWT 토큰 검증
        try {
            InvitationUtil.validateToken(invitation.getToken());
            log.info("JWT 토큰 검증 성공: {}", invitation.getToken());
        } catch (Exception e) {
            log.error("JWT 토큰 검증 실패: {}", e.getMessage());
            throw new IllegalArgumentException("유효하지 않은 초대 코드입니다. (JWT 토큰 검증 실패)");
        }

        // 4. 유효한 토큰 반환
        return invitation.getToken();
    }

    public InvitationDTO addMemberToGroup(Long memberId, Long careRecipientId) {
        try {
            // 1. Member와 CareRecipient 엔터티 조회
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("적절하지 않은 회원 ID: " + memberId));
            CareRecipient careRecipient = careRecipientRepository.findById(careRecipientId)
                    .orElseThrow(() -> new IllegalArgumentException("적절하지 않은 돌봄 대상 ID: " + careRecipientId));

            // 2. CareAssignment 생성 및 설정
            CareAssignment careAssignment = new CareAssignment();
            careAssignment.setMember(member);
            careAssignment.setRecipient(careRecipient);
            careAssignment.setRelationship("가입된 멤버");
            careAssignment.setEmail(member.getEmail()); // 이메일 설정

            // 3. CareAssignment 저장
            CareAssignment savedAssignment = careAssignmentRepository.save(careAssignment);

            log.info("저장된 careAssigmnet"+savedAssignment);
            // 4. 양방향 관계 설정 (Optional)
            careRecipient.addCareAssignment(savedAssignment);

            // 5. InvitationDTO로 변환하여 반환
            return new InvitationDTO(
                    savedAssignment.getId(),
                    savedAssignment.getMember().getId(),
                    savedAssignment.getEmail(),
                    savedAssignment.getRecipient().getId(),
                    savedAssignment.getRelationship()
            );
        } catch (Exception e) {
            log.error("추가 실패. Member ID: {}, CareRecipient ID: {}, Error: {}",
                    memberId, careRecipientId, e.getMessage());
            throw new RuntimeException("CareAssignment 추가 실패", e);
        }
    }

}

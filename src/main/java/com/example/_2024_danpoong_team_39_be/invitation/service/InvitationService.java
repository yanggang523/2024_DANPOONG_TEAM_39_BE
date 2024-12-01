package com.example._2024_danpoong_team_39_be.invitation.service;

import com.example._2024_danpoong_team_39_be.domain.CareAssignment;
import com.example._2024_danpoong_team_39_be.domain.CareRecipient;
import com.example._2024_danpoong_team_39_be.invitation.domain.InvitationCode;
import com.example._2024_danpoong_team_39_be.invitation.repository.InvitationCodeRepository;
import com.example._2024_danpoong_team_39_be.invitation.util.InvitationUtil;
import com.example._2024_danpoong_team_39_be.login.repository.CareRecipientRepository;
import com.example._2024_danpoong_team_39_be.careAssignment.CareAssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvitationService {

    private static final long EXPIRATION_TIME = 3600000; // 1시간

    @Autowired
    private CareRecipientRepository careRecipientRepository;

    @Autowired
    private CareAssignmentRepository careAssignmentRepository;

    @Autowired
    private InvitationCodeRepository invitationCodeRepository;

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
        InvitationCode invitation = invitationCodeRepository.findByInviteCode(code)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 초대 코드입니다."));
        InvitationUtil.validateToken(invitation.getToken());
        return invitation.getToken();
    }

    public boolean addUserToGroup(Long userId, Long groupId) {
        // 사용자 그룹 추가 로직 구현
        return true; // 성공 시 true 반환
    }
}

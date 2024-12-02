package com.example._2024_danpoong_team_39_be.invitation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvitationDTO {
    private Long id; // CareAssignment의 ID
    private Long memberId; // 회원 ID
    private String memberEmail; // 회원 이메일
    private Long careRecipientId; // 대상자 ID
    private String relationship; // 관계

    public InvitationDTO(Long id, Long memberId, String memberEmail, Long careRecipientId, String relationship) {
        this.id = id;
        this.memberId = memberId;
        this.memberEmail = memberEmail;
        this.careRecipientId = careRecipientId;
        this.relationship = relationship;
    }

    @Getter
    @Setter
    public static class InvitationRequest {
        private Long recipientId; // 초대받는 사람 ID
        private Long assignmentId; // 할당 ID
    }
}

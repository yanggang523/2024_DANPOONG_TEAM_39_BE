package com.example._2024_danpoong_team_39_be.invitation.domain;

import com.example._2024_danpoong_team_39_be.domain.CareAssignment;
import com.example._2024_danpoong_team_39_be.domain.CareRecipient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Entity
@NoArgsConstructor // 기본 생성자
public class InvitationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String inviteCode; // 초대 코드
    private String token; // JWT 토큰

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "care_assignment_id", nullable = false)
    private CareAssignment careAssignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "care_recipient_id", nullable = false)
    private CareRecipient careRecipient;

    public InvitationCode(String inviteCode, String token, CareAssignment careAssignment, CareRecipient careRecipient) {
        this.inviteCode = inviteCode;
        this.token = token;
        this.careAssignment = careAssignment;
        this.careRecipient = careRecipient;
    }
}

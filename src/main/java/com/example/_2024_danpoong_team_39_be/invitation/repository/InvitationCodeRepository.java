package com.example._2024_danpoong_team_39_be.invitation.repository;

import com.example._2024_danpoong_team_39_be.invitation.domain.InvitationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvitationCodeRepository extends JpaRepository<InvitationCode, Long> {

    // 초대 코드로 InvitationCode 객체를 찾는 메서드
    Optional<InvitationCode> findByInviteCode(String code);
}


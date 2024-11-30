package com.example._2024_danpoong_team_39_be.login.service;

import com.example._2024_danpoong_team_39_be.careAssignment.CareAssignmentRepository;
import com.example._2024_danpoong_team_39_be.domain.CareAssignment;
import com.example._2024_danpoong_team_39_be.domain.CareRecipient;
import com.example._2024_danpoong_team_39_be.domain.Member;
import com.example._2024_danpoong_team_39_be.login.converter.CareRecipientConverter;
import com.example._2024_danpoong_team_39_be.login.dto.CareRecipientDTO;
import com.example._2024_danpoong_team_39_be.login.repository.CareRecipientRepository;
import com.example._2024_danpoong_team_39_be.login.repository.MemberRepository;
import com.example._2024_danpoong_team_39_be.login.util.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class CareRecipentService {

    private final CareRecipientRepository careRecipientRepository;
    private final CareAssignmentRepository careAssignmentRepository;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

//
//    private CareRecipient createCareRecipient(String token, CareRecipientDTO.CareRecipientProfile careRecipientProfile) {
//        String email = jwtUtil.getEmailFromToken(token);
//
//        // Member 검색
//        Member member = memberRepository.findByEmail(email) // 로그인할 때 새롭게 발급받은 jwt 토큰 사용
//                .orElseThrow(() -> new IllegalArgumentException(email + " 해당 이메일을 사용하는 유저를 찾을 수 없습니다."));
//
//        // CareRecipient 생성
//        CareRecipient newCareRecipient = CareRecipientConverter.toCareRecipient(
//                careRecipientProfile.getCareRecipientid(),
//                careRecipientProfile.getName(),
//                careRecipientProfile.getDiagnosis(),
//                careRecipientProfile.getMobilty_status(),
//                careRecipientProfile.getStart_sleep_time(),
//                careRecipientProfile.getEnd_sleep_time(),
//                careRecipientProfile.getAddress(),
//                careRecipientProfile.getAvg_sleep_time()
//        );
//
//        CareRecipient careRecipient = CareRecipient.create(newCareRecipient, Collections.singletonList(member));
//
//        member.setCareRecipient(careRecipient);
//
//        // 모든 데이터 저장
//        careRecipientRepository.save(newCareRecipient);
//
//        return careRecipientRepository.save(newCareRecipient);
//    }

    @Transactional
    public CareRecipient createCareRecipient(String token, CareRecipientDTO.CareRecipientProfile careRecipientProfile, String relationship) {
        // 1. 토큰에서 이메일 추출
        String email = jwtUtil.getEmailFromToken(token);

        // 2. Member 검색
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 사용하는 유저를 찾을 수 없습니다: " + email));

        // 3. CareRecipient 확인 (ID가 존재하는 경우 DB에서 검색)
        CareRecipient careRecipient;
        if (careRecipientProfile.getId() != null) {
            careRecipient = careRecipientRepository.findById(careRecipientProfile.getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "해당 id를 사용하는 CareRecipient를 찾을 수 없습니다: " + careRecipientProfile.getId()));
        } else {
            // 4. 새로운 CareRecipient 생성
            careRecipient = CareRecipientConverter.toCareRecipient(careRecipientProfile);
            careRecipientRepository.save(careRecipient);
        }

        // 5. CareAssignment 중복 확인
//        boolean isAssignmentExists = careAssignmentRepository.existsByMemberAndRecipientAndRelationship(
//                member, careRecipient, relationship
//        );

//        if (isAssignmentExists) {
//            throw new IllegalStateException("이미 존재하는 CareAssignment입니다: Member = " + member.getId() +
//                    ", CareRecipient = " + careRecipient.getId() + ", Relationship = " + relationship);
//        }

        // 6. CareAssignment 생성
        CareAssignment careAssignment = CareAssignment.create(member, careRecipient, relationship);
        careAssignment.setMember(member);
        careAssignment.setRecipient(careRecipient);
        careAssignment.setRelationship(relationship);

        // 7. CareRecipient에 CareAssignment 추가 (양방향 관계 설정)
        careRecipient.addCareAssignment(careAssignment);

        // 8. 저장
        careRecipientRepository.save(careRecipient); // CareRecipient 저장
        careAssignmentRepository.save(careAssignment); // CareAssignment 저장

        log.info("CareRecipient 및 CareAssignment 저장 완료: {}, {}", careRecipient, careAssignment);
        return careRecipient;
    }
}

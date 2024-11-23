package com.example._2024_danpoong_team_39_be.login.service;

import com.example._2024_danpoong_team_39_be.domain.CalendarStrap;
import com.example._2024_danpoong_team_39_be.domain.CareRecipient;
import com.example._2024_danpoong_team_39_be.domain.Member;
import com.example._2024_danpoong_team_39_be.login.converter.CareRecipientConverter;
import com.example._2024_danpoong_team_39_be.login.dto.CareRecipientDTO;
import com.example._2024_danpoong_team_39_be.login.repository.CareRecipientRepository;
import com.example._2024_danpoong_team_39_be.login.repository.MemberRepository;
import com.example._2024_danpoong_team_39_be.login.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CareRecipentService {

    private final CareRecipientRepository careRecipientRepository;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;


    private CareRecipient createCareRecipient(String token, CareRecipientDTO.CareRecipientProfile careRecipientProfile) {
        String email = jwtUtil.getEmailFromToken(token);

        // Member 검색
        Member member = memberRepository.findByEmail(email) // 로그인할 때 새롭게 발급받은 jwt 토큰 사용
                .orElseThrow(() -> new IllegalArgumentException(email + " 해당 이메일을 사용하는 유저를 찾을 수 없습니다."));

        // CareRecipient 생성
        CareRecipient newCareRecipient = CareRecipientConverter.toCareRecipient(
                careRecipientProfile.getId(),
                careRecipientProfile.getName(),
                careRecipientProfile.getDiagnosis(),
                careRecipientProfile.getMobilty_status(),
                careRecipientProfile.getStart_sleep_time(),
                careRecipientProfile.getEnd_sleep_time(),
                careRecipientProfile.getAddress(),
                careRecipientProfile.getAvg_sleep_time()
        );

        // CalendarStrap 생성 (정적 팩토리 메서드 사용)
        CalendarStrap calendarStrap = CalendarStrap.create(newCareRecipient, Collections.singletonList(member));

        // Member와 CalendarStrap의 연관 관계 설정
        member.setCalendarStrap(calendarStrap);

        // 모든 데이터 저장
        careRecipientRepository.save(newCareRecipient);

        return careRecipientRepository.save(newCareRecipient);
    }

}

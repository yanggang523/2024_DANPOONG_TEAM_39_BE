package com.example._2024_danpoong_team_39_be.login.service;

import com.example._2024_danpoong_team_39_be.domain.Member;
import com.example._2024_danpoong_team_39_be.login.converter.MemberConverter;
import com.example._2024_danpoong_team_39_be.login.dto.MemberRequestDTO;
import com.example._2024_danpoong_team_39_be.login.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository profileRepository;

    private Member updateMember(MemberRequestDTO.FillupRequestDTO memberProfile) {
        Member updateMember = MemberConverter.toMember(
                memberProfile.getName(),
                memberProfile.getAlias(),
                memberProfile.getAge(),
                memberProfile.getGender(),
                memberProfile.getEmail(),
                memberProfile.getProfileImage()
        );

        return profileRepository.save(updateMember) ;

    }
}
//Member newMember = AuthConverter.toMember(
//        kakaoProfile.getKakao_account().getEmail(),
//        kakaoProfile.getKakao_account().getProfile().getNickname()
//
//);
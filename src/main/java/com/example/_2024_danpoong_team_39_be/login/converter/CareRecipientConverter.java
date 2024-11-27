package com.example._2024_danpoong_team_39_be.login.converter;


import com.example._2024_danpoong_team_39_be.domain.CareRecipient;
import com.example._2024_danpoong_team_39_be.login.dto.CareRecipientDTO;

public class CareRecipientConverter {
    public static CareRecipientDTO.CareRecipientProfile toCareRecipient(CareRecipient careRecipient) {
        if (careRecipient == null) {
            return null;
        }

        return CareRecipientDTO.CareRecipientProfile.builder()
                .id(careRecipient.getId())
                .name(careRecipient.getName())
                .diagnosis(careRecipient.getDiagnosis())
                .mobilty_status(careRecipient.getMobilityStatus())
                .start_sleep_time(careRecipient.getStartSleepTime())
                .end_sleep_time(careRecipient.getEndSleepTime())
                .address(careRecipient.getAddress())
                .avg_sleep_time(careRecipient.getAvgSleepTime())
                .build();
    }

    // CareRecipientDTO.CareRecipientProfile -> CareRecipient 변환
    public static CareRecipient toCareRecipient(CareRecipientDTO.CareRecipientProfile careRecipientProfile) {
        return CareRecipient.builder()
                .id(careRecipientProfile.getId())
                .name(careRecipientProfile.getName())
                .diagnosis(careRecipientProfile.getDiagnosis())
                .mobilityStatus(careRecipientProfile.getMobilty_status())
                .startSleepTime(careRecipientProfile.getStart_sleep_time())
                .endSleepTime(careRecipientProfile.getEnd_sleep_time())
                .address(careRecipientProfile.getAddress())
                .avgSleepTime(careRecipientProfile.getAvg_sleep_time())
                .build();
    }



}

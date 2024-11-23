package com.example._2024_danpoong_team_39_be.login.converter;


import com.example._2024_danpoong_team_39_be.domain.CareRecipient;
import com.example._2024_danpoong_team_39_be.login.dto.CareRecipientDTO;

import java.time.LocalTime;

public class CareRecipientConverter {
    public static CareRecipientDTO.CareRecipientProfile toCareRecipient(CareRecipient careRecipient) {
        if (careRecipient == null) {
            return null;
        }

        return CareRecipientDTO.CareRecipientProfile.builder()
                .id(careRecipient.getCareRecipientId())
                .name(careRecipient.getName())
                .diagnosis(careRecipient.getDiagnosis())
                .mobilty_status(careRecipient.getMobilityStatus())
                .start_sleep_time(careRecipient.getStartSleepTime())
                .end_sleep_time(careRecipient.getEndSleepTime())
                .address(careRecipient.getAddress())
                .avg_sleep_time(careRecipient.getAvgSleepTime())
                .build();
    }

    public static CareRecipient toCareRecipient(Long id, String name, String diagnosis, String mobilityStatus,
                                                LocalTime startSleepTime, LocalTime endSleepTime, String address,
                                                LocalTime avgSleepTime) {
        return CareRecipient.builder()
                .careRecipientId(id)
                .name(name)
                .diagnosis(diagnosis)
                .mobilityStatus(mobilityStatus)
                .startSleepTime(startSleepTime)
                .endSleepTime(endSleepTime)
                .address(address)
                .avgSleepTime(avgSleepTime)
                .build();
    }


}

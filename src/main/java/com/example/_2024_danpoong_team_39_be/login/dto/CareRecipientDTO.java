package com.example._2024_danpoong_team_39_be.login.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;


public class CareRecipientDTO {

    @Getter
    @Setter
    @Builder
    public static class CareRecipientProfile {
        private Long id;
        private String name;
        private String diagnosis;
        private String mobilty_status;
        private LocalTime start_sleep_time;
        private LocalTime end_sleep_time;
        private String address;
        private LocalTime avg_sleep_time;
    }


//    @Getter
//    @Setter
//    @Builder
//    public static class CareRecipientProfile {
//        private Long careRecipientid;
//        private String name;
//        private String diagnosis;
//        private String mobilty_status;
//        private LocalTime start_sleep_time;
//        private LocalTime end_sleep_time;
//        private String address;
//        private LocalTime avg_sleep_time;
//    }

}

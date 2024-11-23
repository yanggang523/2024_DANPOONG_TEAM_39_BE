package com.example._2024_danpoong_team_39_be.domain;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CareRecipient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "care_recipient_id")
    private Long careRecipientId;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "diagnosis", length = 50)
    private String diagnosis;

    @Column(name = "mobility_status", length = 50)
    private String mobilityStatus;

    @Column(name = "start_sleep_time")
    private LocalTime startSleepTime;

    @Column(name = "end_sleep_time")
    private LocalTime endSleepTime;

    @Column(name = "address", length = 100)
    private String address;

    @Column(name = "avg_sleep_time")
    private LocalTime avgSleepTime;

    // 연관관계
    @OneToOne(mappedBy = "recipient", cascade = CascadeType.ALL, orphanRemoval = true)
    private CareAssignment careAssignment;

    // 프로필 이미지 없습니다! (추후 추가 예정)

    public CareRecipient(
            Long id, String name, String diagnosis, String mobilityStatus, LocalTime startSleepTime, LocalTime endSleepTime, String address, LocalTime avgSleepTime) {
        this.careRecipientId = id;
        this.name = name;
        this.diagnosis = diagnosis;
        this.mobilityStatus = mobilityStatus;
        this.startSleepTime = startSleepTime;
        this.endSleepTime = endSleepTime;
        this.address = address;
        this.avgSleepTime = avgSleepTime;
    }

}


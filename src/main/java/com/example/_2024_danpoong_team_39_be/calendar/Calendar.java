package com.example._2024_danpoong_team_39_be.calendar;


import com.example._2024_danpoong_team_39_be.careAssignment.CareAssignmentRepository;
import com.example._2024_danpoong_team_39_be.careCalendar.hospital.Hospital;
import com.example._2024_danpoong_team_39_be.careCalendar.meal.Meal;
import com.example._2024_danpoong_team_39_be.careCalendar.medication.Medication;
import com.example._2024_danpoong_team_39_be.careCalendar.others.Others;
import com.example._2024_danpoong_team_39_be.careCalendar.rest.Rest;
import com.example._2024_danpoong_team_39_be.domain.CareAssignment;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Array;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity

public class Calendar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 기본 키

    @Column(nullable = false)
    private String title; // 일정 제목
    private String eventType; // 일정타입 공부, 팀플 등등
    @Column(nullable = false)
    private LocalTime startTime; // 시작 시간
    @Column(nullable = false)
    private LocalTime endTime; // 종료 시간
    @Column(nullable = false)
    private LocalDate date; // 날짜

    @Enumerated(EnumType.STRING)
    private RepeatCycle repeatCycle;
    private Boolean isAllday;
    private Boolean isAlarm; // 알림 여부
    private String location; // 위치
    private String memo; // 메모
    private Boolean isShared; // 공유 여부

    // Enum 클래스: 반복 주기 정의
    public enum RepeatCycle {
        DAILY, WEEKLY, MONTHLY
    }

    // Many-to-One 관계 설정, 여러 Calendar가 하나의 CareAssignment에 연결될 수 있도록 합니다.
    // CareAssignment와의 단방향 관계
    @ManyToOne
    @JoinColumn(name = "care_assignment_id")
    private CareAssignment careAssignment;

    @Column(name = "care_assignment_id", insertable = false, updatable = false)
    private Long careAssignmentId;


    // 카테고리별 추가 정보 (식사, 병원, 휴식, 복약, 내일정)
    @OneToOne(mappedBy = "calendar", cascade = CascadeType.ALL)
    private Meal meal;

    @OneToOne(mappedBy = "calendar", cascade = CascadeType.ALL)
    private Hospital hospital;

    @OneToOne(mappedBy = "calendar", cascade = CascadeType.ALL)
    private Rest rest;

    @OneToMany(mappedBy = "calendar", cascade = CascadeType.ALL)
   private List<Medication> medications = new ArrayList<>();

    @OneToMany(mappedBy = "calendar", cascade = CascadeType.ALL)
    private List<Others> others = new ArrayList<>();

    @OneToOne(mappedBy = "calendar", cascade = CascadeType.ALL)
    private MyCalendar myCalendar;

    private String category;

    @OneToMany(mappedBy = "calendar")
    private List<CareAssignment> careAssignments;
    // CareAssignment 리스트 설정 메서드
    public void setCareAssignments(List<CareAssignment> careAssignments) {
        this.careAssignments = careAssignments;

    }
}

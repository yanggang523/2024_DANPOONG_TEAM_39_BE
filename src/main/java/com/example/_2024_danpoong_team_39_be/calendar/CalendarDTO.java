package com.example._2024_danpoong_team_39_be.calendar;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class CalendarDTO {
    private String title;
    private String eventType;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate date;
    private Boolean isAllday;
    private Boolean isAlarm;
    private String location;
    private String memo;
    private Boolean isShared;
    private Long careAssignmentId;
    private Calendar.RepeatCycle repeatCycle;
    private String category = "myCalendar";
    // Getters and Setters
}
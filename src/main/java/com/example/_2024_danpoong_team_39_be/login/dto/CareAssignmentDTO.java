package com.example._2024_danpoong_team_39_be.login.dto;

import com.example._2024_danpoong_team_39_be.domain.CareRecipient;
import com.example._2024_danpoong_team_39_be.domain.Member;

import java.time.LocalDate;
import java.time.LocalTime;

public class CareAssignmentDTO {
    private Member member;
    private CareRecipient recipient;
    private String relationship;

    // Calendar 정보
    private String calendarTitle;
    private LocalDate calendarDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean isAllday;
    private Boolean isAlarm;
    private String category;

    // Getters and Setters
    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public CareRecipient getRecipient() {
        return recipient;
    }

    public void setRecipient(CareRecipient recipient) {
        this.recipient = recipient;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getCalendarTitle() {
        return calendarTitle;
    }

    public void setCalendarTitle(String calendarTitle) {
        this.calendarTitle = calendarTitle;
    }

    public LocalDate getCalendarDate() {
        return calendarDate;
    }

    public void setCalendarDate(LocalDate calendarDate) {
        this.calendarDate = calendarDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Boolean getIsAllday() {
        return isAllday;
    }

    public void setIsAllday(Boolean isAllday) {
        this.isAllday = isAllday;
    }

    public Boolean getIsAlarm() {
        return isAlarm;
    }

    public void setIsAlarm(Boolean isAlarm) {
        this.isAlarm = isAlarm;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

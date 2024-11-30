package com.example._2024_danpoong_team_39_be.calendar;

import java.util.List;
import java.util.stream.Collectors;

public class CalendarConverter {
    public static CalendarDTO toCalendarDTO(Calendar calendar) {
        CalendarDTO calendarDTO = new CalendarDTO();
        calendarDTO.setTitle(calendar.getTitle());
        calendarDTO.setEventType(calendar.getEventType());
        calendarDTO.setStartTime(calendar.getStartTime());
        calendarDTO.setEndTime(calendar.getEndTime());
        calendarDTO.setDate(calendar.getDate());
        calendarDTO.setIsAllday(calendar.getIsAllday());
        calendarDTO.setIsAlarm(calendar.getIsAlarm());
        calendarDTO.setLocation(calendar.getLocation());
        calendarDTO.setMemo(calendar.getMemo());
        calendarDTO.setIsShared(calendar.getIsShared());
        calendarDTO.setCareAssignmentId(calendar.getCareAssignment().getId());
        return calendarDTO;
    }
    public static List<CalendarDTO> toCalendarDTOList(List<Calendar> calendars) {
        return calendars.stream()
                .map(CalendarConverter::toCalendarDTO)
                .collect(Collectors.toList());
    }
}

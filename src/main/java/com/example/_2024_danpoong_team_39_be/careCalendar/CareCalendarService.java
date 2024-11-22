package com.example._2024_danpoong_team_39_be.careCalendar;


import com.example._2024_danpoong_team_39_be.calendar.Calendar;
import com.example._2024_danpoong_team_39_be.calendar.CalendarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CareCalendarService
{
    @Autowired
    private CalendarRepository calendarRepository;
    // isShared가 true인 모든 일정 조회
    public List<Calendar> getAllSharedCalendars() {
        return calendarRepository.findByIsSharedTrue();  // isShared가 true인 모든 일정 조회
    }
    // 날짜별로 돌봄 캘린더의 공유된 일정 조회
    public List<Calendar> getCareCalendarEventsByDate(LocalDate date) {
        // 기본 캘린더에서 isShared가 true인 일정만 조회
        return calendarRepository.findByDateAndIsSharedTrue(date);
    }

    // 주간 단위로 돌봄 캘린더의 공유된 일정 조회
    public List<Calendar> getWeeklyCareCalendarEvents(LocalDate date) {
        // 해당 날짜가 속한 주의 시작일(일요일)을 계산
        LocalDate startOfWeek = date.minusDays(date.getDayOfWeek().getValue() % 7);

        // 해당 주의 모든 날짜를 저장할 리스트
        List<LocalDate> weekDates = startOfWeek.datesUntil(startOfWeek.plusDays(7))
                .toList();

        // startDate가 해당 주의 날짜인 일정 조회
        return calendarRepository.findByDateInAndIsSharedTrue(weekDates);
    }

}

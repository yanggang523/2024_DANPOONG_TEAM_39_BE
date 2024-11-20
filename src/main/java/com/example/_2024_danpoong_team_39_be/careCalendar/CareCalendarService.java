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

    // 날짜별로 돌봄 캘린더의 공유된 일정 조회
    public List<Calendar> getCareCalendarEventsByDate(LocalDate date) {
        // 기본 캘린더에서 isShared가 true인 일정만 조회
        List<Calendar> events = calendarRepository.findByDateAndIsSharedTrue(date);

        // 공백 시간 추가
        return addBlankTimeSlots(events, date);
    }

    // 주간 단위로 돌봄 캘린더의 공유된 일정 조회
    public List<Calendar> getWeeklyCareCalendarEvents(LocalDate date) {
        // 해당 날짜가 속한 주의 시작일(일요일)을 계산
        LocalDate startOfWeek = date.minusDays(date.getDayOfWeek().getValue() % 7);

        // 해당 주의 모든 날짜를 저장할 리스트
        List<LocalDate> weekDates = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            weekDates.add(startOfWeek.plusDays(i));
        }

        // startDate가 해당 주의 날짜인 일정 조회
        List<Calendar> events = calendarRepository.findByDateInAndIsSharedTrue(weekDates);

        // 공백 시간 추가 (필요에 따라 구현)
        return addBlankTimeSlots(events, startOfWeek);
    }


    // 일정 사이에 공백 시간을 삽입하는 메소드
    private List<Calendar> addBlankTimeSlots(List<Calendar> events, LocalDate date) {
        List<Calendar> result = new ArrayList<>();

        // 먼저 기존 일정을 시간순으로 정렬
        events.sort((e1, e2) -> {
            // null 처리 추가: startTime이 null인 경우 비교하지 않도록 처리
            LocalTime startTime1 = e1.getStartTime() == null ? LocalTime.MIN : e1.getStartTime();
            LocalTime startTime2 = e2.getStartTime() == null ? LocalTime.MIN : e2.getStartTime();
            return startTime1.compareTo(startTime2);
        });

        // 이전 일정 종료 시간을 추적
        LocalTime previousEndTime = LocalTime.MIN; // 자정부터 시작

        // 각 일정에 대해 확인
        for (Calendar event : events) {
            // startTime이 null인 경우 건너뛰기 (또는 처리 방법에 따라 예외를 던지거나 기본값을 설정)
            if (event.getStartTime() == null || event.getEndTime() == null) {
                continue;  // startTime이 null인 일정을 건너뜁니다.
            }

            // 이전 일정 종료 시간이 현재 일정 시작 시간 이전이면, 그 사이에 공백 일정 추가
            if (event.getStartTime().isAfter(previousEndTime)) {
                // 공백 시간 생성
                Calendar blankEvent = new Calendar();
                blankEvent.setTitle("공백일정");
                blankEvent.setDate(date);
                blankEvent.setStartTime(previousEndTime);
                blankEvent.setEndTime(event.getStartTime());
                blankEvent.setIsAlarm(false);   // 공백시간은 알람이 없음
                blankEvent.setIsShared(false);  // 공백시간은 공유되지 않음
                result.add(blankEvent);
            }

            // 실제 공유 일정 추가
            result.add(event);
            previousEndTime = event.getEndTime();  // 이전 일정 종료 시간 업데이트
        }

        // 하루 24시간 동안 공백일정 추가 (자정부터 마지막 일정까지)
        if (previousEndTime.isBefore(LocalTime.of(23, 59))) {
            Calendar blankEvent = new Calendar();
            blankEvent.setTitle("공백일정");
            blankEvent.setDate(date);
            blankEvent.setStartTime(previousEndTime);
            blankEvent.setEndTime(LocalTime.of(23, 59));  // 마지막 시간은 23:59로 설정
            blankEvent.setIsAlarm(false);
            blankEvent.setIsShared(false);
            result.add(blankEvent);
        }

        // 공백 시간들이 연속된 경우 합치기
        List<Calendar> mergedResult = mergeContinuousBlankSlots(result);

        return mergedResult;
    }

    // 공백 시간이 연속된 경우 합치는 메소드
    private List<Calendar> mergeContinuousBlankSlots(List<Calendar> events) {
        List<Calendar> mergedEvents = new ArrayList<>();
        Calendar previousBlankEvent = null;

        for (Calendar event : events) {
            if ("공백일정".equals(event.getTitle())) {
                // 이전 공백일정과 이어지면 합침
                if (previousBlankEvent != null && previousBlankEvent.getEndTime().equals(event.getStartTime())) {
                    previousBlankEvent.setEndTime(event.getEndTime());  // 시간 합침
                } else {
                    // 이어지지 않으면 새로 추가
                    if (previousBlankEvent != null) {
                        mergedEvents.add(previousBlankEvent);
                    }
                    previousBlankEvent = event;
                }
            } else {
                // 공백일정이 아닌 일정을 추가
                if (previousBlankEvent != null) {
                    mergedEvents.add(previousBlankEvent);
                    previousBlankEvent = null;
                }
                mergedEvents.add(event);
            }
        }

        // 마지막 남은 공백일정 추가
        if (previousBlankEvent != null) {
            mergedEvents.add(previousBlankEvent);
        }

        return mergedEvents;
    }

}

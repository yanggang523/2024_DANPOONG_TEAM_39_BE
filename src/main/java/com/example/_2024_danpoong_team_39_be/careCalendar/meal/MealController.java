package com.example._2024_danpoong_team_39_be.careCalendar.meal;

import com.example._2024_danpoong_team_39_be.calendar.Calendar;
import com.example._2024_danpoong_team_39_be.calendar.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
@RestController
@RequestMapping("/api/careCalendar/meal")
public class MealController {
    @Autowired
    private CalendarService calendarService;  // Service 사용


    // 특정 날짜의 세부 일정 조회
    @GetMapping("/{calendar_id}")
    public Calendar getDailyDetailEvents(@PathVariable Long calendar_id) {
        return calendarService.getDailyDetailEventsWithId(calendar_id);
    }

    // 특정 날짜의 일정 추가
    @PostMapping("")
    public Calendar addEvent( @RequestBody Calendar calendar) {
        if (calendar.getIsShared() == null) {
            calendar.setIsShared(true);
        }
        // 카테고리가 비어있으면 "Meal"로 설정
        if (calendar.getCategory() == null || calendar.getCategory().isEmpty()) {
            calendar.setCategory("meal");
        }
        return calendarService.addEvent(calendar);
    }

    // 일정 수정 (부분 수정, PATCH 사용)
    @PatchMapping("/{calendar_id}")
    public Calendar updateEvent(@PathVariable Long calendar_id, @RequestBody Calendar calendar) {
        return calendarService.updateEvent(calendar_id,  calendar);  // 서비스 사용
    }

    // 일정 삭제
    @DeleteMapping("/{calendar_id}")
    public boolean deleteEvent(@PathVariable Long calendar_id) {
        return calendarService.deleteEvent(calendar_id);  // 서비스 사용
    }
}


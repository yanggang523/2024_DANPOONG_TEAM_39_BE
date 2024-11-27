package com.example._2024_danpoong_team_39_be.careCalendar;


import com.example._2024_danpoong_team_39_be.calendar.Calendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/careCalendar")
public class CareCalendarController {
    @Autowired
    private CareCalendarService careCalendarService;
    //전체 공유 일정
    @GetMapping("/all")
    public List<Calendar> getAllSharedCareCalendarEvents() {
        return careCalendarService.getAllSharedCalendars();
    }
    //일일 공유 일정
    @GetMapping("/{date}")
    public List<Calendar> getCareEventsByDate(@PathVariable LocalDate date) {
        return careCalendarService.getCareCalendarEventsByDate(date);
    }

    @PatchMapping("/events/{eventId}")
    public ResponseEntity<Calendar> partialUpdateEvent(
            @PathVariable Long eventId,
            @RequestBody Calendar updatedCalendar,
            @RequestParam Long userId
    ) {
        try {
            Calendar updatedEvent = careCalendarService.updateCalendar(eventId, updatedCalendar, userId); // Pass userId to the service method
            if (updatedEvent != null) {
                return ResponseEntity.ok(updatedEvent);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId, @PathVariable Long userId) {
        try {
            careCalendarService.deleteCalendar(eventId, userId);
            return ResponseEntity.noContent().build(); // 삭제 성공 시 204 반환
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build(); // 일정이 존재하지 않으면 404 반환
        }
    }

}

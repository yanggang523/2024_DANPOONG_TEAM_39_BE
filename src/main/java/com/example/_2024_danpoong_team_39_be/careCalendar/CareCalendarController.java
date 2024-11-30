package com.example._2024_danpoong_team_39_be.careCalendar;


import com.example._2024_danpoong_team_39_be.calendar.Calendar;
import com.example._2024_danpoong_team_39_be.calendar.CalendarRepository;
import com.example._2024_danpoong_team_39_be.careAssignment.CareAssignmentRepository;
import com.example._2024_danpoong_team_39_be.domain.CareAssignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/careCalendar")
public class CareCalendarController {

    @Autowired
    private CareAssignmentRepository careAssignmentRepository;
    @Autowired
    private CareCalendarService careCalendarService;
    @Autowired
    private CalendarRepository calendarRepository;

    //공유일정 전체 조회(weekly, daily)
    @GetMapping("/all")
    public List<Calendar> getAllSharedCareCalendarEvents() {
        return careCalendarService.getAllSharedCalendars();
    }
    //특정 일정 상세 조회
    @GetMapping("/{id}")
    public Calendar getCalendarById(@PathVariable Long id) {
        // ID를 기준으로 해당 Calendar 객체 조회
        Calendar calendar = calendarRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 캘린더를 찾을 수 없습니다. ID: " + id));

        // 조회된 캘린더 반환
        return calendar;
    }
    //돌봄일정 작성 폼
    @GetMapping
    public Calendar getCalendarForm() {
        Calendar calendar = new Calendar();
        // 모든 필드가 빈 상태로 설정된 새로운 Calendar 객체를 반환합니다.
        return calendar;
    }
    //돌봄일정 저장
    @PostMapping
    public Calendar createCalendar(@RequestBody Calendar calendar) {
        Long careAssignmentId = calendar.getCareAssignmentId(); // careAssignmentId 추출

        if (careAssignmentId == null) {
            throw new IllegalArgumentException("돌보미가 없어요");
        }

        // CareAssignment 조회
        CareAssignment careAssignment = careAssignmentRepository.findById(careAssignmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 돌보미를 찾을 수 없습니다: " + careAssignmentId));

        // CareAssignment를 Calendar에 설정
        calendar.setCareAssignment(careAssignment);

        // Calendar 저장
        return careCalendarService.createCalendarForAssignment(careAssignmentId, calendar);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCalendar(@PathVariable Long id) {
        try {
            careCalendarService.deleteCalendar(id);
            return ResponseEntity.ok("일정이 성공적으로 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("삭제 실패: " + e.getMessage());
        }


    }
    @PatchMapping("/{id}")
    public ResponseEntity<Calendar> patchCalendar(@PathVariable Long id, @RequestBody Calendar updatedCalendar) {
        try {
            // 일정 업데이트
            Calendar calendar = careCalendarService.updateCalendar(id, updatedCalendar);

            if (calendar != null) {
                return ResponseEntity.ok(calendar);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


}

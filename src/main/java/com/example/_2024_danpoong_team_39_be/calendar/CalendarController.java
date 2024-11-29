package com.example._2024_danpoong_team_39_be.calendar;

import com.example._2024_danpoong_team_39_be.careCalendar.CareCalendarService;
import com.example._2024_danpoong_team_39_be.domain.CareAssignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {
    @Autowired
    private CalendarService careAssignmentService;
  @Autowired
  private CareCalendarService careCalendarService;
    @Autowired
    private CalendarRepository calendarRepository;
    // 특정 날짜의 세부 일정 조회(1개) 작동 o
    @GetMapping("/{date}/events/{eventId}")
    public List<Calendar> getDailyDetailEvents(@PathVariable LocalDate date, @PathVariable Long eventId) {
        return careAssignmentService.getDailyDetailEvents(date, eventId);
    }
    // 특정 날짜의 일정 리스트 조회(ex 2024-11-23에 있는 모든 a의 일정)
    @GetMapping("/{careAssignmentId}/{date}")
    public List<Calendar> getDailyEventsForMembers(@RequestParam Long careAssignmentId, @PathVariable LocalDate date) {
        return careAssignmentService.getDailyEventsForMembers(careAssignmentId, date);
    }
    // 회원별로 일정 리스트 조회 회원 아이디를
    @GetMapping("/{careAssignmentId}/events")
    public ResponseEntity<List<Calendar>> getEventsByMember(@PathVariable Long careAssignmentId) {
        // 해당 회원의 일정을 조회
        List<Calendar> calendars = calendarRepository.findCalendarByCareAssignmentId(careAssignmentId);
        return ResponseEntity.ok(calendars);
    }
    //일정추가폼
    @GetMapping("")
    public Object getEmptyCalendarForm(@RequestParam LocalDate date,
                                       @RequestParam LocalTime startTime,
                                       @RequestParam LocalTime endTime) {
        // 빈 시간대에 돌보미만 반환하는 로직 추가
        Object result = careCalendarService.getCareAssignmentsForBlankSlot(date, startTime, endTime);

        // 결과가 "빈 시간대에 돌보미가 없습니다."라는 메시지라면 메시지 반환
        if (result instanceof String) {
            return result; // "빈 시간대에 돌보미가 없습니다." 메시지 반환
        }

        // 돌보미가 있을 경우 Calendar 객체 생성하여 반환
        Calendar calendar = new Calendar();
        calendar.setCaregiver((List<CareAssignment>) result);  // 돌보미 리스트 설정
        return calendar;
    }
    // 일정 추가
    @PostMapping("")
    public ResponseEntity<Calendar> addEvent(@RequestBody Calendar calendar, Principal principal) {
        try {
            System.out.println("addEvent 메서드 호출됨");
            System.out.println("로그인 사용자: " + principal.getName());  // principal 수정됨
            System.out.println("전송된 이메일: " + calendar.getCareAssignment().getEmail());

            // 이메일이 일치하는지 확인
            String loggedInUserEmail = principal.getName(); // 로그인한 사용자의 이메일

            if (!loggedInUserEmail.equals(calendar.getCareAssignment().getEmail())) {
                // 이메일이 일치하지 않으면 권한 없음 처리
                System.out.println(principal.getName());  // principal 수정됨
                System.out.println(calendar.getCareAssignment().getEmail());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }

            // 기본 값 설정
            if (calendar.getIsShared() == null) {
                calendar.setIsShared(false);
            }
            if (calendar.getCategory() == null || calendar.getCategory().isEmpty()) {
                calendar.setCategory("myCalendar");
            }
            if (calendar.getCareAssignment() == null || calendar.getCareAssignment().getEmail() == null) {
                System.out.println(principal.getName());  // principal 수정됨
                System.out.println(calendar.getCareAssignment().getEmail());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // 잘못된 요청
            }

            System.out.println(principal.getName());  // principal 수정됨
            System.out.println(calendar.getCareAssignment().getEmail());

            // 서비스 메서드 호출
            Calendar savedCalendar = careAssignmentService.addEvent(calendar, loggedInUserEmail);
            return ResponseEntity.ok(savedCalendar);
        } catch (IllegalArgumentException e) {
            System.out.println(principal.getName());  // principal 수정됨
            System.out.println(calendar.getCareAssignment().getEmail());
            return ResponseEntity.badRequest().body(null); // 잘못된 요청 처리
        }
    }


    // 일정 수정
    @PatchMapping("/events/{eventId}")
    public ResponseEntity<Calendar> partialUpdateEvent(@PathVariable Long eventId, @RequestBody Calendar updatedCalendar, Principal principal) {
        try {
            System.out.println("addEvent 메서드 호출됨");
            System.out.println("로그인 사용자: " + principal.getName());  // principal 수정됨
            System.out.println("전송된 이메일: " + updatedCalendar.getCareAssignment().getEmail());

            // 이메일이 일치하는지 확인
            String loggedInUserEmail = principal.getName(); // 로그인한 사용자의 이메일

            if (!loggedInUserEmail.equals(updatedCalendar.getCareAssignment().getEmail())) {
                // 이메일이 일치하지 않으면 권한 없음 처리
                System.out.println(principal.getName());  // principal 수정됨
                System.out.println(updatedCalendar.getCareAssignment().getEmail());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
            System.out.println("addEvent 메서드 호출됨");
            System.out.println("로그인 사용자: " + principal.getName());  // principal 수정됨
            System.out.println("전송된 이메일: " + updatedCalendar.getCareAssignment().getEmail());
            Calendar updatedEvent = careAssignmentService.updateEvent(eventId, updatedCalendar, loggedInUserEmail);
            if (updatedEvent != null) {
                return ResponseEntity.ok(updatedEvent);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 일정 삭제
    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId, Principal principal) {
        // principal에서 로그인한 사용자의 이메일을 가져옵니다.
        String email = principal.getName();

        try {
            // 이메일과 이벤트 ID를 기반으로 일정 삭제 요청을 서비스에 전달
            careAssignmentService.deleteEvent(eventId, email);

            // 삭제 성공 시 204 No Content 반환
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            // 예외 발생 시, 일정이 없거나 이메일이 일치하지 않는 경우
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // 권한 오류 발생 시 403 반환
        }
    }


}

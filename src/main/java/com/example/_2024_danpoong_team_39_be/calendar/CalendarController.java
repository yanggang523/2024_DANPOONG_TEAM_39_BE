package com.example._2024_danpoong_team_39_be.calendar;
import com.example._2024_danpoong_team_39_be.login.BaseResponse;
import com.example._2024_danpoong_team_39_be.login.util.JwtUtil;
import com.example._2024_danpoong_team_39_be.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {
    private final CalendarService calendarService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private NotificationService notificationService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }
//    내일정 캘린더 폼
    @GetMapping("")
    public CalendarDTO showCalendarForm() {
        CalendarDTO calendarDTO = new CalendarDTO();
        return calendarDTO;
    }
//    캘린더 저장
@PostMapping
public BaseResponse<CalendarDTO> createCalendar(@RequestHeader("Authorization") String token,
                                                @RequestBody CalendarDTO calendarDTO) {
    if (calendarDTO.getCategory() == null) {
        calendarDTO.setCategory("myCalendar");
    }
    Calendar calendar = calendarService.createCalendar(token, calendarDTO);
    notificationService.notifyCalendar(calendar);
    return BaseResponse.onSuccess(CalendarConverter.toCalendarDTO(calendar));
}

    //돌보미 1명의 전체 캘린더 조회
    @GetMapping("/{careAssignmentId}")
    public BaseResponse<List<Calendar>> getAllCalendarsForAssignment(@PathVariable Long careAssignmentId) {
        // CareAssignment에 속한 모든 일정 조회
        List<Calendar> calendars = calendarService.getAllCalendarsForAssignment(careAssignmentId);
        return BaseResponse.onSuccess(calendars);
    }

    @GetMapping("/myCalendar")
    public BaseResponse<List<Calendar>> getMyCalendar(@RequestHeader("Authorization") String token) {
        try {
            // 토큰에서 이메일 추출
            String email = jwtUtil.getEmailFromToken(token);
            List<Calendar> calendars = calendarService.getCalendarsForUser(email);
            return new BaseResponse<>(true, calendars, "일정 조회에 성공했습니다.");
        } catch (Exception e) {
            // 에러 응답 반환
            return new BaseResponse<>(false, null, "일정을 조회하는 도중 에러가 발생했습니다: " + e.getMessage());
        }
    }



    // 일정 수정 (부분 수정)
    @PatchMapping("/{calendarId}")
    public BaseResponse<CalendarDTO> updateCalendar(@PathVariable Long calendarId,
                                                    @RequestHeader("Authorization") String token,
                                                    @RequestBody CalendarDTO calendarDTO) {
        // 부분 수정에 대한 로직
        Calendar updatedCalendar = calendarService.updateCalendarPartial(calendarId, token, calendarDTO);
        return BaseResponse.onSuccess(CalendarConverter.toCalendarDTO(updatedCalendar));
    }

    //
    // 일정 삭제
    @DeleteMapping("/{calendarId}")
    public BaseResponse<Void> deleteCalendar(@PathVariable Long calendarId,
                                             @RequestHeader("Authorization") String token) {
        calendarService.deleteCalendar(calendarId, token);
        return BaseResponse.onSuccess(null);
    }
//}


}

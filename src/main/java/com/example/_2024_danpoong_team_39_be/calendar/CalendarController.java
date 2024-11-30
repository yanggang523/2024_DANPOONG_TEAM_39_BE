package com.example._2024_danpoong_team_39_be.calendar;

import com.example._2024_danpoong_team_39_be.domain.CareAssignment;
import com.example._2024_danpoong_team_39_be.login.BaseResponse;
import com.example._2024_danpoong_team_39_be.login.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {
    private final CalendarService calendarService;
    @Autowired
    private JwtUtil jwtUtil;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }
//    내일정 캘린더 폼
    @GetMapping("")
    public CalendarDTO showCalendarForm() {
        // 빈 CalendarDTO 객체를 반환
        CalendarDTO calendarDTO = new CalendarDTO();
        return calendarDTO;
    }
//    캘린더 저장
    @PostMapping
    public BaseResponse<CalendarDTO> createCalendar(@RequestHeader("Authorization") String token,
                                                    @RequestBody CalendarDTO calendarDTO) {
        // Calendar 객체를 생성하는 서비스 호출
        Calendar calendar = calendarService.createCalendar(token, calendarDTO);
        if (calendar.getIsShared() == null) {
            calendar.setIsShared(false);  // true로 설정
        }

        // 생성된 Calendar 객체를 CalendarDTO로 변환하여 반환
        return BaseResponse.onSuccess(CalendarConverter.toCalendarDTO(calendar));
    }

//돌보미 1명의 전체 캘린더 조회
    @GetMapping("/{careAssignmentId}")
    public BaseResponse<List<CalendarDTO>> getAllCalendarsForAssignment(@PathVariable Long careAssignmentId) {
        // CareAssignment에 속한 모든 일정 조회
        List<Calendar> calendars = calendarService.getAllCalendarsForAssignment(careAssignmentId);

        // DTO로 변환하여 반환
        List<CalendarDTO> calendarDTOs = CalendarConverter.toCalendarDTOList(calendars);

        return BaseResponse.onSuccess(calendarDTOs);
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
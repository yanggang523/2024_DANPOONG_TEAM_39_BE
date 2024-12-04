
package com.example._2024_danpoong_team_39_be.calendar;

import com.example._2024_danpoong_team_39_be.careAssignment.CareAssignmentRepository;
import com.example._2024_danpoong_team_39_be.domain.CareAssignment;
import com.example._2024_danpoong_team_39_be.login.util.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalendarService {
    private final CalendarRepository calendarRepository;
    private final CareAssignmentRepository careAssignmentRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public CalendarService(CalendarRepository calendarRepository,
                           CareAssignmentRepository careAssignmentRepository,
                           JwtUtil jwtUtil) {
        this.calendarRepository = calendarRepository;
        this.careAssignmentRepository = careAssignmentRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public Calendar createCalendar(String token, CalendarDTO calendarDTO) {
        // 토큰에서 이메일 추출
        String email = jwtUtil.getEmailFromToken(token);

        // 이메일을 통해 CareAssignment 조회
        CareAssignment careAssignment = careAssignmentRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 사용하는 CareAssignment를 찾을 수 없습니다: " + email));

        // Calendar 생성
        Calendar calendar = new Calendar();
        calendar.setTitle(calendarDTO.getTitle());
        calendar.setEventType(calendarDTO.getEventType());
        calendar.setStartTime(calendarDTO.getStartTime());
        calendar.setEndTime(calendarDTO.getEndTime());
        calendar.setDate(calendarDTO.getDate());
        calendar.setIsAllday(calendarDTO.getIsAllday());
        calendar.setIsAlarm(calendarDTO.getIsAlarm());
        calendar.setLocation(calendarDTO.getLocation());
        calendar.setMemo(calendarDTO.getMemo());
        calendar.setCareAssignment(careAssignment);  // CareAssignment와 연결

        // 반복 일정 처리
        if (calendarDTO.getRepeatCycle() != null) {
            // 반복 일정 날짜 생성
            List<LocalDate> repeatDates = generateRepeatDates(calendarDTO.getDate(), calendarDTO.getRepeatCycle());
            for (LocalDate repeatDate : repeatDates) {
                Calendar repeatEvent = new Calendar();
                repeatEvent.setTitle(calendarDTO.getTitle());
                repeatEvent.setEventType(calendarDTO.getEventType());
                repeatEvent.setStartTime(calendarDTO.getStartTime());
                repeatEvent.setEndTime(calendarDTO.getEndTime());
                repeatEvent.setDate(repeatDate);
                repeatEvent.setIsAllday(calendarDTO.getIsAllday());
                repeatEvent.setIsAlarm(calendarDTO.getIsAlarm());
                repeatEvent.setLocation(calendarDTO.getLocation());
                repeatEvent.setMemo(calendarDTO.getMemo());
                repeatEvent.setIsShared(calendarDTO.getIsShared());
                repeatEvent.setCareAssignment(careAssignment); // CareAssignment 설정
                repeatEvent.setRepeatCycle(calendarDTO.getRepeatCycle()); // 반복 주기 설정

                // 반복 일정 저장
                calendarRepository.save(repeatEvent);
            }
        } else {
            // 단일 일정 처리
            if (calendar.getDate() == null) {
                calendar.setDate(LocalDate.now()); // 기본 날짜 설정
            }

            if (Boolean.TRUE.equals(calendar.getIsShared())) {
                calendar.setCareAssignment(careAssignment); // 여기에 CareAssignment 설정
            }

            // 저장
            calendarRepository.save(calendar);
        }

        return calendar;
    }

    // 반복 주기에 따라 날짜 리스트 생성
    private List<LocalDate> generateRepeatDates(LocalDate startDate, Calendar.RepeatCycle repeatCycle) {
        List<LocalDate> repeatDates = new ArrayList<>();
        // 반복 횟수를 적절히 설정 (예: 최대 12번)
        int repeatCount = 12; // 예시로 12번 반복

        switch (repeatCycle) {
            case WEEKLY:
                for (int i = 0; i < repeatCount; i++) {
                    repeatDates.add(startDate.plusWeeks(i)); // 매주 반복
                }
                break;
            case DAILY:
                for (int i = 0; i < repeatCount; i++) {
                    repeatDates.add(startDate.plusDays(i)); // 매일 반복
                }
                break;
            case MONTHLY:
                for (int i = 0; i < repeatCount; i++) {
                    repeatDates.add(startDate.plusMonths(i)); // 매달 반복
                }
                break;
            default:
                throw new IllegalArgumentException("지원되지 않는 반복 주기입니다.");
        }
        return repeatDates;
    }



    @Transactional
    public List<Calendar> getAllCalendarsForAssignment(Long careAssignmentId) {
        // CareAssignment를 ID로 조회
        CareAssignment careAssignment = careAssignmentRepository.findById(careAssignmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 CareAssignment를 찾을 수 없습니다: " + careAssignmentId));

        // CareAssignment에 연결된 모든 일정 조회
        List<Calendar> calendars = calendarRepository.findByCareAssignment(careAssignment);

        // 결과 반환
        return calendars;
    }



    @Transactional
    public Calendar updateCalendarPartial(Long calendarId, String token, CalendarDTO calendarDTO) {
        // 토큰에서 이메일 추출
        String email = jwtUtil.getEmailFromToken(token);

        // 이메일을 통해 CareAssignment 조회
        CareAssignment careAssignment = careAssignmentRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 사용하는 CareAssignment를 찾을 수 없습니다: " + email));

        // 기존 Calendar 객체 조회
        Calendar existingCalendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new IllegalArgumentException("해당 Calendar를 찾을 수 없습니다: " + calendarId));


        // CalendarDTO에서 수정된 필드만 업데이트
        if (calendarDTO.getTitle() != null) {
            existingCalendar.setTitle(calendarDTO.getTitle());
        }
        if (calendarDTO.getEventType() != null) {
            existingCalendar.setEventType(calendarDTO.getEventType());
        }
        if (calendarDTO.getStartTime() != null) {
            existingCalendar.setStartTime(calendarDTO.getStartTime());
        }
        if (calendarDTO.getEndTime() != null) {
            existingCalendar.setEndTime(calendarDTO.getEndTime());
        }
        if (calendarDTO.getDate() != null) {
            existingCalendar.setDate(calendarDTO.getDate());
        }
        if (calendarDTO.getIsAllday() != null) {
            existingCalendar.setIsAllday(calendarDTO.getIsAllday());
        }
        if (calendarDTO.getIsAlarm() != null) {
            existingCalendar.setIsAlarm(calendarDTO.getIsAlarm());
        }
        if (calendarDTO.getLocation() != null) {
            existingCalendar.setLocation(calendarDTO.getLocation());
        }
        if (calendarDTO.getMemo() != null) {
            existingCalendar.setMemo(calendarDTO.getMemo());
        }

        // 반복 일정 처리 (필요한 경우만 수정)
        if (calendarDTO.getRepeatCycle() != null) {
            // 기존 반복 일정을 삭제하고 새로 생성
            List<LocalDate> repeatDates = generateRepeatDates(calendarDTO.getDate(), calendarDTO.getRepeatCycle());
            for (LocalDate repeatDate : repeatDates) {
                Calendar repeatEvent = new Calendar();
                repeatEvent.setTitle(existingCalendar.getTitle());
                repeatEvent.setEventType(existingCalendar.getEventType());
                repeatEvent.setStartTime(existingCalendar.getStartTime());
                repeatEvent.setEndTime(existingCalendar.getEndTime());
                repeatEvent.setDate(repeatDate);
                repeatEvent.setIsAllday(existingCalendar.getIsAllday());
                repeatEvent.setIsAlarm(existingCalendar.getIsAlarm());
                repeatEvent.setLocation(existingCalendar.getLocation());
                repeatEvent.setMemo(existingCalendar.getMemo());
                repeatEvent.setCareAssignment(careAssignment);
                repeatEvent.setRepeatCycle(calendarDTO.getRepeatCycle());

                // 반복 일정 저장
                calendarRepository.save(repeatEvent);
            }
        }

        // 업데이트된 일정 저장
        calendarRepository.save(existingCalendar);

        return existingCalendar;
    }


    @Transactional
    public void deleteCalendar(Long calendarId, String token) {
        // 토큰에서 이메일 추출
        String email = jwtUtil.getEmailFromToken(token);

        // 이메일을 통해 CareAssignment 조회
        CareAssignment careAssignment = careAssignmentRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 사용하는 CareAssignment를 찾을 수 없습니다: " + email));

        // 해당 Calendar 조회
        Calendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 일정을 찾을 수 없습니다: " + calendarId));

        // 해당 CareAssignment와 일정을 연결한 후 삭제 권한 확인
        if (!calendar.getCareAssignment().equals(careAssignment)) {
            throw new IllegalArgumentException("이 일정을 삭제할 권한이 없습니다.");
        }

        // Calendar 삭제
        calendarRepository.delete(calendar);
    }
}

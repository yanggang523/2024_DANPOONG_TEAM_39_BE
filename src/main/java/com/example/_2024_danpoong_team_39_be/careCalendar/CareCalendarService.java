package com.example._2024_danpoong_team_39_be.careCalendar;


import com.example._2024_danpoong_team_39_be.calendar.Calendar;
import com.example._2024_danpoong_team_39_be.calendar.CalendarRepository;
import com.example._2024_danpoong_team_39_be.domain.CareAssignment;
import com.example._2024_danpoong_team_39_be.domain.CareRecipient;
import com.example._2024_danpoong_team_39_be.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//돌봄일정인 경우 같은 어르신을 둔 경우 자유롭게 crud가능
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
        List<Calendar> events = calendarRepository.findByDateAndIsSharedTrue(date);

        // 공백시간 삽입
        return addBlankTimeSlots(events, date);
    }

    // 일정 수정 (isShared가 true인 경우만)
    public Calendar updateCalendar(Long calendarId, Calendar updatedCalendar, Long userId) {
        // 기존의 Calendar 객체를 조회
        Optional<Calendar> existingCalendarOpt = calendarRepository.findById(calendarId);

        if (existingCalendarOpt.isPresent()) {
            Calendar existingCalendar = existingCalendarOpt.get();

            // isShared가 true일 때만 수정 가능
            if (!existingCalendar.getIsShared()) {
                throw new IllegalArgumentException("공유된 일정만 수정할 수 있습니다.");
            }

            // 수정 권한을 확인 (현재 사용자가 수정 가능한지 확인)
            if (!isUserAuthorizedToUpdate(existingCalendar, userId)) {
                throw new IllegalArgumentException("수정 권한이 없습니다.");
            }

            // 수정할 속성들 업데이트
            existingCalendar.setTitle(updatedCalendar.getTitle());
            existingCalendar.setEventType(updatedCalendar.getEventType());
            existingCalendar.setStartTime(updatedCalendar.getStartTime());
            existingCalendar.setEndTime(updatedCalendar.getEndTime());
            existingCalendar.setDate(updatedCalendar.getDate());
            existingCalendar.setRepeatCycle(updatedCalendar.getRepeatCycle());
            existingCalendar.setIsAllday(updatedCalendar.getIsAllday());
            existingCalendar.setIsAlarm(updatedCalendar.getIsAlarm());
            existingCalendar.setLocation(updatedCalendar.getLocation());
            existingCalendar.setMemo(updatedCalendar.getMemo());
            existingCalendar.setIsShared(updatedCalendar.getIsShared());
            existingCalendar.setCategory(updatedCalendar.getCategory());
            existingCalendar.setEmail(updatedCalendar.getEmail());
            existingCalendar.setName(updatedCalendar.getName());

            // CareAssignment와 관련된 정보가 변경된다면 여기도 업데이트
            if (updatedCalendar.getCareAssignment() != null) {
                existingCalendar.setCareAssignment(updatedCalendar.getCareAssignment());
            }

            // Calendar와 관련된 다른 엔티티들도 필요시 수정
            if (updatedCalendar.getMeal() != null) {
                existingCalendar.setMeal(updatedCalendar.getMeal());
            }

            if (updatedCalendar.getHospital() != null) {
                existingCalendar.setHospital(updatedCalendar.getHospital());
            }

            if (updatedCalendar.getRest() != null) {
                existingCalendar.setRest(updatedCalendar.getRest());
            }

            if (updatedCalendar.getMedication() != null) {
                existingCalendar.setMedication(updatedCalendar.getMedication());
            }

            if (updatedCalendar.getOthers() != null) {
                existingCalendar.setOthers(updatedCalendar.getOthers());
            }

            // 변경 사항 저장
            return calendarRepository.save(existingCalendar);
        } else {
            throw new IllegalArgumentException("해당 ID의 일정이 존재하지 않습니다.");
        }
    }

    private boolean isUserAuthorizedToUpdate(Calendar calendar, Long userId) {
        // calendar의 careAssignment를 통해 careRecipient를 가져옴
        CareAssignment careAssignment = calendar.getCareAssignment();

        // careAssignment에 연결된 careRecipient를 가져옴
        CareRecipient careRecipient = careAssignment.getRecipient();

        // 해당 careRecipient에 속한 careAssignments와 그에 연결된 멤버들만 수정 가능
        // careRecipient의 careAssignment에 연결된 멤버가 userId와 일치하는지 확인
        if (careRecipient.getCareAssignments().stream()
                .anyMatch(assignment -> assignment.getMember().getId().equals(userId))) {
            return true; // userId가 해당 careRecipient의 careAssignments에 포함된 멤버인 경우
        }

        // 일치하지 않으면 수정 권한이 없음
        return false;
    }



    // 일정 삭제 (isShared가 true인 경우만)
    public void deleteCalendar(Long calendarId, Long userId) {
        // 삭제할 Calendar 조회
        Optional<Calendar> calendarOpt = calendarRepository.findById(calendarId);

        if (calendarOpt.isPresent()) {
            Calendar calendar = calendarOpt.get();

            // 해당 Calendar가 CareAssignment와 연결된 CareRecipient와 일치하는지 확인
            CareAssignment careAssignment = calendar.getCareAssignment();
            if (careAssignment == null || !careAssignment.getRecipient().getCareRecipientId().equals(userId)) {
                throw new IllegalArgumentException("이 일정은 해당 CareRecipient와 연결되지 않았거나 권한이 없습니다.");
            }

            // isShared가 true일 때만 삭제 가능
            if (!calendar.getIsShared()) {
                throw new IllegalArgumentException("공유된 일정만 삭제할 수 있습니다.");
            }

            // 관련된 모든 엔티티들 삭제 (필요시)
            if (calendar.getMeal() != null) {
                // Meal 삭제 로직 추가 (필요시)
            }
            if (calendar.getHospital() != null) {
                // Hospital 삭제 로직 추가 (필요시)
            }
            if (calendar.getRest() != null) {
                // Rest 삭제 로직 추가 (필요시)
            }
            if (calendar.getMedication() != null) {
                // Medication 삭제 로직 추가 (필요시)
            }
            if (calendar.getOthers() != null) {
                // Others 삭제 로직 추가 (필요시)
            }

            // Calendar 삭제
            calendarRepository.delete(calendar);
        } else {
            throw new IllegalArgumentException("해당 ID의 일정이 존재하지 않습니다.");
        }
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
    public Object getCareAssignmentsForBlankSlot(LocalDate date, LocalTime startTime, LocalTime endTime) {
        // 해당 날짜와 시간 범위 내에 있는 모든 CareAssignment를 조회
        List<CareAssignment> assignments = calendarRepository.findCareAssignmentsByDateAndTimeRange(date, startTime, endTime);

        // 빈 시간대만 필터링
        List<CareAssignment> emptyCaregivers = assignments.stream()
                .filter(ca -> ca.getCalendars().isEmpty())  // 일정이 없는 돌보미만 필터링
                .collect(Collectors.toList());

        // 빈 돌보미가 없다면 메시지 반환
        if (emptyCaregivers.isEmpty()) {
            return "빈 시간대에 돌보미가 없습니다.";
        }

        // 돌보미가 있으면 돌보미 목록 반환
        return emptyCaregivers;
    }
}
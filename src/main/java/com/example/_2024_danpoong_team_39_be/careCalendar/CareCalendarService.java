package com.example._2024_danpoong_team_39_be.careCalendar;


import com.example._2024_danpoong_team_39_be.calendar.Calendar;
import com.example._2024_danpoong_team_39_be.calendar.CalendarRepository;
import com.example._2024_danpoong_team_39_be.careAssignment.CareAssignmentRepository;
import com.example._2024_danpoong_team_39_be.domain.CareAssignment;
import com.example._2024_danpoong_team_39_be.domain.CareRecipient;

import com.example._2024_danpoong_team_39_be.domain.Member;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

//돌봄일정인 경우 같은 어르신을 둔 경우 자유롭게 crud가능
@Service
public class CareCalendarService {
    @Autowired
    private CalendarRepository calendarRepository;
    @Autowired
    private CareAssignmentRepository careAssignmentRepository;

    // isShared가 true인 모든 일정 조회
    public List<Calendar> getAllSharedCalendars() {
        return calendarRepository.findByIsSharedTrue();  // isShared가 true인 모든 일정 조회
    }

    public List<CareAssignment> getCareAssignments() {
        return careAssignmentRepository.findAll(); // careAssignmentRepository는 실제 데이터베이스에서 데이터를 가져오는 역할을 합니다.
    }

    @Transactional
    public Calendar createCalendarForAssignment(Long careAssignmentId, Calendar calendar) {
        System.out.println("Looking for CareAssignment with ID: " + careAssignmentId);

        CareAssignment careAssignment = null;

        // careAssignmentId가 null이 아니면 CareAssignment 조회
        if (careAssignmentId != null) {
            careAssignment = careAssignmentRepository.findById(careAssignmentId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 CareAssignment를 찾을 수 없습니다: " + careAssignmentId));
        }

        // CareAssignment가 null인 경우, careAssignment를 null로 설정
        calendar.setCareAssignment(careAssignment);

        // 반복 일정 처리
        if (calendar.getRepeatCycle() != null) {
            LocalDate startDate = calendar.getDate();
            if (startDate == null) {
                throw new IllegalArgumentException("반복 일정은 시작 날짜(startDate)가 필요합니다.");
            }

            // 반복 일정 날짜 생성
            List<LocalDate> repeatDates = generateRepeatDates(startDate, calendar.getRepeatCycle());
            for (LocalDate repeatDate : repeatDates) {
                Calendar repeatEvent = new Calendar();
                repeatEvent.setDate(repeatDate);
                repeatEvent.setIsAlarm(calendar.getIsAlarm());
                repeatEvent.setTitle(calendar.getTitle());
                repeatEvent.setEventType(calendar.getEventType());
                repeatEvent.setStartTime(calendar.getStartTime());
                repeatEvent.setEndTime(calendar.getEndTime());
                repeatEvent.setIsAllday(calendar.getIsAllday());
                repeatEvent.setMemo(calendar.getMemo());
                repeatEvent.setLocation(calendar.getLocation());
                repeatEvent.setRepeatCycle(calendar.getRepeatCycle());
                repeatEvent.setIsShared(calendar.getIsShared());
                repeatEvent.setCategory(calendar.getCategory());
                repeatEvent.setCareAssignment(careAssignment); // 반복 일정에 동일한 CareAssignment 설정

                // 저장
                calendarRepository.save(repeatEvent);
            }
            System.out.println("반복 일정: " + repeatDates);
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
        switch (repeatCycle) {
            case WEEKLY:
                for (int i = 1; i <= 7; i++) {
                    repeatDates.add(startDate.plusDays(i - 1));
                }
                break;
            case DAILY:
                repeatDates.add(startDate);
                break;
            case MONTHLY:
                for (int i = 0; i < 30; i++) {
                    repeatDates.add(startDate.plusDays(i));
                }
                break;
            default:
                throw new IllegalArgumentException("지원되지 않는 반복 주기입니다.");
        }
        return repeatDates;
    }


    @Transactional
    public Calendar updateCalendar(Long id, Calendar updatedCalendar) {
        Optional<Calendar> existingCalendarOpt = calendarRepository.findById(id);
        if (existingCalendarOpt.isPresent()) {
            Calendar calendar = existingCalendarOpt.get();


            // 기본적인 필드 업데이트
            updateBasicFields(updatedCalendar, calendar);

            // 반복 일정 처리
            handleRepeatCycle(updatedCalendar, calendar);

            return calendarRepository.save(calendar);
        }
        return null;  // 존재하지 않으면 null 반환
    }

    private void updateBasicFields(Calendar updatedCalendar, Calendar calendar) {
        if (updatedCalendar.getTitle() != null) {
            calendar.setTitle(updatedCalendar.getTitle());
        }
        if (updatedCalendar.getEventType() != null) {
            calendar.setEventType(updatedCalendar.getEventType());
        }
        if (updatedCalendar.getStartTime() != null) {
            calendar.setStartTime(updatedCalendar.getStartTime());
        }
        if (updatedCalendar.getEndTime() != null) {
            calendar.setEndTime(updatedCalendar.getEndTime());
        }
        if (updatedCalendar.getLocation() != null) {
            calendar.setLocation(updatedCalendar.getLocation());
        }
        if (updatedCalendar.getMemo() != null) {
            calendar.setMemo(updatedCalendar.getMemo());
        }
        if (updatedCalendar.getIsAllday() != null) {
            calendar.setIsAllday(updatedCalendar.getIsAllday());
        }
        if (updatedCalendar.getIsAlarm() != null) {
            calendar.setIsAlarm(updatedCalendar.getIsAlarm());
        }
        if (updatedCalendar.getIsShared() != null) {
            calendar.setIsShared(updatedCalendar.getIsShared());
        }
    }

    private void handleRepeatCycle(Calendar updatedCalendar, Calendar calendar) {
        if (updatedCalendar.getRepeatCycle() != null) {
            LocalDate startDate = updatedCalendar.getDate();  // 단일 날짜만 사용
            if (startDate == null) {
                throw new IllegalArgumentException("반복 일정은 startDate가 필요합니다.");
            }

            // 반복 날짜 계산 (예시로 startDate만 갱신하도록 설정)
            LocalDate repeatDate = calculateRepeatDate(updatedCalendar.getRepeatCycle(), startDate);
            calendar.setDate(repeatDate);  // 계산된 반복 날짜로 업데이트
        } else {
            LocalDate today = LocalDate.now();
            calendar.setDate(today);  // 반복이 아니면 오늘 날짜로 설정
        }
    }

    private LocalDate calculateRepeatDate(Calendar.RepeatCycle repeatCycle, LocalDate startDate) {
        switch (repeatCycle) {
            case WEEKLY:
                return startDate.plusWeeks(1);  // 1주일 뒤로 설정
            case DAILY:
                return startDate.plusDays(1);  // 하루 뒤로 설정
            case MONTHLY:
                return startDate.plusMonths(1);  // 1개월 뒤로 설정
            default:
                throw new IllegalArgumentException("지원되지 않는 반복 주기입니다.");
        }
    }


    @Transactional
    public void deleteCalendar(Long id) {
        Calendar calendar = calendarRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 일정을 찾을 수 없습니다: " + id));
        calendarRepository.delete(calendar);
    }

    public String checkAvailableCaregiversForEmptySlot(LocalDate date, LocalTime startTime, LocalTime endTime) {
        // 1. 모든 CareAssignment 가져오기
        List<CareAssignment> allCareAssignments = careAssignmentRepository.findAll();

        // 2. 특정 날짜와 시간대에 일정이 있는 CareAssignment ID 가져오기
        List<Long> busyCaregiverIds = calendarRepository.findByDate(date).stream()
                .filter(calendar -> !(calendar.getEndTime().isBefore(startTime) || calendar.getStartTime().isAfter(endTime)))
                .map(calendar -> calendar.getCareAssignment().getId())
                .distinct()
                .toList();

        // 3. 일정이 없는 CareAssignment 반환
        List<CareAssignment> availableCaregivers = allCareAssignments.stream()
                .filter(careAssignment -> !busyCaregiverIds.contains(careAssignment.getId()))
                .toList();

        // 4. availableCaregivers의 개수로 돌보미 유무 체크
        if (availableCaregivers.size() > 0) {
            return "돌보미가 있습니다";
        } else {
            return "돌보미가 없습니다";
        }
    }





}

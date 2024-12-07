package com.example._2024_danpoong_team_39_be.careCalendar;


import com.example._2024_danpoong_team_39_be.calendar.Calendar;
import com.example._2024_danpoong_team_39_be.calendar.CalendarRepository;
import com.example._2024_danpoong_team_39_be.careAssignment.CareAssignmentRepository;
import com.example._2024_danpoong_team_39_be.careCalendar.hospital.TransportationTpye;
import com.example._2024_danpoong_team_39_be.careCalendar.meal.MealType;
import com.example._2024_danpoong_team_39_be.careCalendar.medication.Medication;
import com.example._2024_danpoong_team_39_be.careCalendar.others.Others;
import com.example._2024_danpoong_team_39_be.careCalendar.rest.RestType;
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
                .filter(calendar -> calendar.getCareAssignment() != null) // CareAssignment가 null이 아닌지 확인
                .filter(calendar -> !(calendar.getEndTime().isBefore(startTime) || calendar.getStartTime().isAfter(endTime)))
                // 겹치는 시간대라도 끝나는 시간과 시작 시간이 정확히 맞아떨어지면 비는 시간으로 간주
                .filter(calendar -> !(calendar.getEndTime().equals(startTime) || calendar.getStartTime().equals(endTime)))
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

    public Map<String, Object> getRestCalendar(LocalDate date, LocalTime startTime, LocalTime endTime) {
        // 휴식 카테고리 일정 생성
        Calendar calendar = new Calendar();
        calendar.setCategory("rest");

        // 해당 날짜의 모든 일정 조회
        List<Calendar> allCalendars = calendarRepository.findByDate(date);

        // 해당 날짜와 시간에 이미 일정이 있는 돌보미 ID 찾기
        Set<Long> busyCaregiverIds = findBusyCaregiverIds(allCalendars, startTime, endTime);

        // 가능한 돌보미 목록 업데이트
        List<CareAssignment> availableAssignments = updateCaregiverAvailability(busyCaregiverIds);

        // Calendar 객체에 가능한 돌보미 설정
        calendar.setCareAssignments(availableAssignments);

        // 응답 객체 생성
        Map<String, Object> response = new HashMap<>();
        response.put("calendar", calendar);  // Calendar 객체
        response.put("restType", getRestTypes());  // RestType 목록 반환
        return response;
    }
    public Map<String, Object> getMedicaionCalendar(LocalDate date, LocalTime startTime, LocalTime endTime) {
        // 휴식 카테고리 일정 생성
        Calendar calendar = new Calendar();
        calendar.setCategory("medication");

        // 해당 날짜의 모든 일정 조회
        List<Calendar> allCalendars = calendarRepository.findByDate(date);

        // 해당 날짜와 시간에 이미 일정이 있는 돌보미 ID 찾기
        Set<Long> busyCaregiverIds = findBusyCaregiverIds(allCalendars, startTime, endTime);

        // 가능한 돌보미 목록 업데이트
        List<CareAssignment> availableAssignments = updateCaregiverAvailability(busyCaregiverIds);

        // Calendar 객체에 가능한 돌보미 설정
        calendar.setCareAssignments(availableAssignments);
        List<String> medicationTypeList = new ArrayList<>();
        for (Medication medication : calendar.getMedications()) {
            medicationTypeList.add(medication.getMedicationType()); // Others 엔티티에서 'othersType' 값을 리스트에 추가
        }

        // 응답 객체 생성
        Map<String, Object> response = new HashMap<>();
        response.put("calendar", calendar);
        response.put("medicationType", medicationTypeList);
        return response;
    }
    public Map<String, Object> getOthersCalendar(LocalDate date, LocalTime startTime, LocalTime endTime) {
        // 휴식 카테고리 일정 생성
        Calendar calendar = new Calendar();
        calendar.setCategory("others");

        // 해당 날짜의 모든 일정 조회
        List<Calendar> allCalendars = calendarRepository.findByDate(date);

        // 해당 날짜와 시간에 이미 일정이 있는 돌보미 ID 찾기
        Set<Long> busyCaregiverIds = findBusyCaregiverIds(allCalendars, startTime, endTime);

        // 가능한 돌보미 목록 업데이트
        List<CareAssignment> availableAssignments = updateCaregiverAvailability(busyCaregiverIds);
        calendar.setCareAssignments(availableAssignments);
        // Calendar 객체에 가능한 돌보미 설정
        // othersType 데이터를 가져와서 응답에 추가
        List<String> othersTypeList = new ArrayList<>();
        for (Others other : calendar.getOthers()) {
            othersTypeList.add(other.getOthersType()); // Others 엔티티에서 'othersType' 값을 리스트에 추가
        }

        Map<String, Object> response = new HashMap<>();
        response.put("calendar", calendar);
        response.put("othersType", othersTypeList);  // othersType 리스트를 'others'로 반환
        return response;
    }
    public Map<String, Object> getHospiatalCalendar(LocalDate date, LocalTime startTime, LocalTime endTime) {
        // 휴식 카테고리 일정 생성
        Calendar calendar = new Calendar();
        calendar.setCategory("hospital");

        // 해당 날짜의 모든 일정 조회
        List<Calendar> allCalendars = calendarRepository.findByDate(date);

        // 해당 날짜와 시간에 이미 일정이 있는 돌보미 ID 찾기
        Set<Long> busyCaregiverIds = findBusyCaregiverIds(allCalendars, startTime, endTime);

        // 가능한 돌보미 목록 업데이트
        List<CareAssignment> availableAssignments = updateCaregiverAvailability(busyCaregiverIds);
        calendar.setCareAssignments(availableAssignments);
        // TransportationTpye의 모든 값을 반환
        List<String> transportationTypes = new ArrayList<>();
        for (TransportationTpye type : TransportationTpye.values()) {
            transportationTypes.add(type.name()); // TransportationTpye enum 값들을 리스트로 저장
        }
        Map<String, Object> response = new HashMap<>();
        response.put("calendar", calendar);
        response.put("transportationType", transportationTypes);  // TransportationTpye 리스트 추가
        return response;
    }
    public Map<String, Object> getMealCalendar(LocalDate date, LocalTime startTime, LocalTime endTime) {
        // 휴식 카테고리 일정 생성
        Calendar calendar = new Calendar();
        calendar.setCategory("meal");

        // 해당 날짜의 모든 일정 조회
        List<Calendar> allCalendars = calendarRepository.findByDate(date);

        // 해당 날짜와 시간에 이미 일정이 있는 돌보미 ID 찾기
        Set<Long> busyCaregiverIds = findBusyCaregiverIds(allCalendars, startTime, endTime);

        // 가능한 돌보미 목록 업데이트
        List<CareAssignment> availableAssignments = updateCaregiverAvailability(busyCaregiverIds);
        calendar.setCareAssignments(availableAssignments);
        // MealType의 모든 값을 반환
        List<String> mealTypes = new ArrayList<>();
        for (MealType type : MealType.values()) {
            mealTypes.add(type.name()); // MealType enum 값들을 리스트로 저장
        }

        Map<String, Object> response = new HashMap<>();
        response.put("calendar", calendar);
        response.put("mealType", mealTypes);  // MealType 리스트 추가
        return response;
    }


    // 일정이 겹치는 돌보미 ID를 찾는 메서드
    private Set<Long> findBusyCaregiverIds(List<Calendar> allCalendars, LocalTime startTime, LocalTime endTime) {
        return allCalendars.stream()
                .filter(cal -> cal.getCareAssignment() != null &&
                        isScheduleConflict(cal.getStartTime(), cal.getEndTime(), startTime, endTime))
                .map(cal -> cal.getCareAssignment().getId())
                .collect(Collectors.toSet());
    }

    // 돌보미의 available 상태를 업데이트하는 메서드
    private List<CareAssignment> updateCaregiverAvailability(Set<Long> busyCaregiverIds) {
        return careAssignmentRepository.findAll().stream()
                .map(assignment -> {
                    boolean isAvailable = !busyCaregiverIds.contains(assignment.getId());
                    assignment.setAvailable(isAvailable);
                    return assignment;
                })
                .collect(Collectors.toList());
    }

    // 일정 충돌 여부 확인 메서드
    private boolean isScheduleConflict(LocalTime scheduleStartTime, LocalTime scheduleEndTime, LocalTime requestStartTime, LocalTime requestEndTime) {
        return !(scheduleEndTime.equals(requestStartTime) ||
                scheduleStartTime.equals(requestEndTime) ||
                scheduleEndTime.isBefore(requestStartTime) ||
                scheduleStartTime.isAfter(requestEndTime));
    }

    // RestType 목록 반환 메서드
    private List<String> getRestTypes() {
        return Arrays.stream(RestType.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }
    public Map<String, Object> prepareRestCalendar() {
        // Calendar 객체 생성
        Calendar calendar = new Calendar();

        // DB에서 모든 CareAssignment 가져오기
        List<CareAssignment> allAssignments = careAssignmentRepository.findAll();
        calendar.setCareAssignments(allAssignments); // 리스트 설정

        // 'available' 값이 false인 경우 강제로 true로 설정
        for (CareAssignment assignment : allAssignments) {
            if (!assignment.isAvailable()) { // available 값이 false인 경우
                assignment.setAvailable(true); // true로 강제 설정
            }
        }

        // RestType의 모든 값을 리스트로 저장
        List<String> restTypes = new ArrayList<>();
        for (RestType type : RestType.values()) {
            restTypes.add(type.name());
        }

        // category는 "rest"로 고정
        calendar.setCategory("rest");

        // careAssignments 내부에 돌보미 정보 포함
        List<Map<String, Object>> careAssignmentsWithDetails = new ArrayList<>();
        for (CareAssignment assignment : allAssignments) {
            Map<String, Object> assignmentDetails = new HashMap<>();
            assignmentDetails.put("id", assignment.getId());

            // 돌보미 정보
            Map<String, Object> memberDetails = new HashMap<>();
            memberDetails.put("id", assignment.getMember().getId());
            memberDetails.put("alias", assignment.getMember().getAlias());
            memberDetails.put("email", assignment.getMember().getEmail());

            assignmentDetails.put("member", memberDetails);
            assignmentDetails.put("relationship", assignment.getRelationship());
            assignmentDetails.put("calendar", assignment.getCalendar()); // 필요에 따라 설정

            careAssignmentsWithDetails.add(assignmentDetails);
        }

        // 최종 응답 객체 준비
        Map<String, Object> response = new HashMap<>();
        response.put("calendar", calendar);
        response.put("restType", restTypes); // RestType 리스트 추가

        return response;
    }
    public Map<String, Object> prepareMedicationCalendar() {
        // Calendar 객체 생성
        Calendar calendar = new Calendar();

        // DB에서 모든 CareAssignment 가져오기
        List<CareAssignment> allAssignments = careAssignmentRepository.findAll();
        calendar.setCareAssignments(allAssignments); // 리스트 설정

        // 'available' 값이 false인 경우 강제로 true로 설정
        for (CareAssignment assignment : allAssignments) {
            if (!assignment.isAvailable()) { // available 값이 false인 경우
                assignment.setAvailable(true); // true로 강제 설정
            }
        }

        List<String> medicationTypeList = new ArrayList<>();
        for (Medication medication : calendar.getMedications()) {
            medicationTypeList.add(medication.getMedicationType()); // Others 엔티티에서 'othersType' 값을 리스트에 추가
        }

        // category는 "rest"로 고정
        calendar.setCategory("medication");
        // 응답으로 반환할 데이터 준비
        Map<String, Object> response = new HashMap<>();
        // careAssignments 내부에 돌보미 정보 포함
        List<Map<String, Object>> careAssignmentsWithDetails = new ArrayList<>();
        for (CareAssignment assignment : allAssignments) {
            Map<String, Object> assignmentDetails = new HashMap<>();
            assignmentDetails.put("id", assignment.getId());

            // 돌보미 정보
            Map<String, Object> memberDetails = new HashMap<>();
            memberDetails.put("id", assignment.getMember().getId());
            memberDetails.put("alias", assignment.getMember().getAlias());
            memberDetails.put("email", assignment.getMember().getEmail());

            assignmentDetails.put("member", memberDetails);
            assignmentDetails.put("relationship", assignment.getRelationship());
            assignmentDetails.put("calendar", assignment.getCalendar()); // 필요에 따라 설정

            careAssignmentsWithDetails.add(assignmentDetails);
        }

        // 최종 응답 객체 준비
        // 최종 응답 객체에 추가
        response.put("calendar", calendar);
        response.put("medicationType", medicationTypeList);  // othersType 리스트를 'others'로 반환

        return response;
    }
    public Map<String, Object> prepareOtherCalendar() {
        // Calendar 객체 생성
        Calendar calendar = new Calendar();

        // DB에서 모든 CareAssignment 가져오기
        List<CareAssignment> allAssignments = careAssignmentRepository.findAll();
        calendar.setCareAssignments(allAssignments); // 리스트 설정

        // 'available' 값이 false인 경우 강제로 true로 설정
        for (CareAssignment assignment : allAssignments) {
            if (!assignment.isAvailable()) { // available 값이 false인 경우
                assignment.setAvailable(true); // true로 강제 설정
            }
        }

        // category는 "rest"로 고정
        calendar.setCategory("others");
        // 응답으로 반환할 데이터 준비
        Map<String, Object> response = new HashMap<>();
        // careAssignments 내부에 돌보미 정보 포함
        List<Map<String, Object>> careAssignmentsWithDetails = new ArrayList<>();
        for (CareAssignment assignment : allAssignments) {
            Map<String, Object> assignmentDetails = new HashMap<>();
            assignmentDetails.put("id", assignment.getId());

            // 돌보미 정보
            Map<String, Object> memberDetails = new HashMap<>();
            memberDetails.put("id", assignment.getMember().getId());
            memberDetails.put("alias", assignment.getMember().getAlias());
            memberDetails.put("email", assignment.getMember().getEmail());

            assignmentDetails.put("member", memberDetails);
            assignmentDetails.put("relationship", assignment.getRelationship());
            assignmentDetails.put("calendar", assignment.getCalendar()); // 필요에 따라 설정

            careAssignmentsWithDetails.add(assignmentDetails);
        }
        // othersType 데이터를 가져와서 응답에 추가
        List<String> othersTypeList = new ArrayList<>();
        for (Others other : calendar.getOthers()) {
            othersTypeList.add(other.getOthersType()); // Others 엔티티에서 'othersType' 값을 리스트에 추가
        }

        // 최종 응답 객체에 추가
        response.put("calendar", calendar);
        response.put("othersType", othersTypeList);  // othersType 리스트를 'others'로 반환
        return response;
    }
    public Map<String, Object> prepareMealCalendar() {
        // Calendar 객체 생성
        Calendar calendar = new Calendar();

        // DB에서 모든 CareAssignment 가져오기
        List<CareAssignment> allAssignments = careAssignmentRepository.findAll();
        calendar.setCareAssignments(allAssignments); // 리스트 설정

        // 'available' 값이 false인 경우 강제로 true로 설정
        for (CareAssignment assignment : allAssignments) {
            if (!assignment.isAvailable()) { // available 값이 false인 경우
                assignment.setAvailable(true); // true로 강제 설정
            }
        }

        // category는 "rest"로 고정
        calendar.setCategory("meal");
        // 응답으로 반환할 데이터 준비
        Map<String, Object> response = new HashMap<>();
        // careAssignments 내부에 돌보미 정보 포함
        List<Map<String, Object>> careAssignmentsWithDetails = new ArrayList<>();
        for (CareAssignment assignment : allAssignments) {
            Map<String, Object> assignmentDetails = new HashMap<>();
            assignmentDetails.put("id", assignment.getId());

            // 돌보미 정보
            Map<String, Object> memberDetails = new HashMap<>();
            memberDetails.put("id", assignment.getMember().getId());
            memberDetails.put("alias", assignment.getMember().getAlias());
            memberDetails.put("email", assignment.getMember().getEmail());

            assignmentDetails.put("member", memberDetails);
            assignmentDetails.put("relationship", assignment.getRelationship());
            assignmentDetails.put("calendar", assignment.getCalendar()); // 필요에 따라 설정

            careAssignmentsWithDetails.add(assignmentDetails);
        }

        // 최종 응답 객체에 추가
        List<String> mealTypes = new ArrayList<>();
        for (MealType type : MealType.values()) {
            mealTypes.add(type.name());  // RestType enum 값들을 리스트로 저장
        }
        // 최종 응답 객체에 추가
        response.put("calendar", calendar);
        response.put("mealType", mealTypes);  // othersType 리스트를 'others'로 반환
        return response;
    }
    public Map<String, Object> prepareHospitalCalendar() {
        // Calendar 객체 생성
        Calendar calendar = new Calendar();

        // DB에서 모든 CareAssignment 가져오기
        List<CareAssignment> allAssignments = careAssignmentRepository.findAll();
        calendar.setCareAssignments(allAssignments); // 리스트 설정

        // 'available' 값이 false인 경우 강제로 true로 설정
        for (CareAssignment assignment : allAssignments) {
            if (!assignment.isAvailable()) { // available 값이 false인 경우
                assignment.setAvailable(true); // true로 강제 설정
            }
        }

        // category는 "rest"로 고정
        calendar.setCategory("rest");
        // 응답으로 반환할 데이터 준비
        Map<String, Object> response = new HashMap<>();
        // careAssignments 내부에 돌보미 정보 포함
        List<Map<String, Object>> careAssignmentsWithDetails = new ArrayList<>();
        for (CareAssignment assignment : allAssignments) {
            Map<String, Object> assignmentDetails = new HashMap<>();
            assignmentDetails.put("id", assignment.getId());

            // 돌보미 정보
            Map<String, Object> memberDetails = new HashMap<>();
            memberDetails.put("id", assignment.getMember().getId());
            memberDetails.put("alias", assignment.getMember().getAlias());
            memberDetails.put("email", assignment.getMember().getEmail());

            assignmentDetails.put("member", memberDetails);
            assignmentDetails.put("relationship", assignment.getRelationship());
            assignmentDetails.put("calendar", assignment.getCalendar()); // 필요에 따라 설정

            careAssignmentsWithDetails.add(assignmentDetails);
        }
        List<String> transportationTypes = new ArrayList<>();
        for (TransportationTpye type : TransportationTpye.values()) {
            transportationTypes.add(type.name());  // RestType enum 값들을 리스트로 저장
        }
        // 최종 응답 객체에 추가
        response.put("calendar", calendar);
        response.put("transportationType", transportationTypes);  // RestType 리스트 추가
        return response;
    }

}

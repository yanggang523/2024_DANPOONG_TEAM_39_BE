package com.example._2024_danpoong_team_39_be.careCalendar;


import com.example._2024_danpoong_team_39_be.calendar.Calendar;
import com.example._2024_danpoong_team_39_be.calendar.CalendarRepository;
import com.example._2024_danpoong_team_39_be.careAssignment.CareAssignmentRepository;

import com.example._2024_danpoong_team_39_be.careAssignment.CareAssignmentService;
import com.example._2024_danpoong_team_39_be.careCalendar.hospital.TransportationTpye;
import com.example._2024_danpoong_team_39_be.careCalendar.meal.MealType;
import com.example._2024_danpoong_team_39_be.careCalendar.medication.Medication;
import com.example._2024_danpoong_team_39_be.careCalendar.others.Others;
import com.example._2024_danpoong_team_39_be.careCalendar.rest.RestType;
import com.example._2024_danpoong_team_39_be.domain.CareAssignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/careCalendar")
public class CareCalendarController {

    @Autowired
    private CareAssignmentRepository careAssignmentRepository;

    @Autowired
    private CareCalendarService careCalendarService;
    @Autowired
    private CalendarRepository calendarRepository;

    @Autowired
    private CareAssignmentService careAssignmentService;


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

    @GetMapping("/dolbomiList")
    public List<CareAssignment> getDolbomiList() {
        // DB에서 모든 CareAssignment 가져오기
        return careAssignmentRepository.findAll();
    }

    @GetMapping("/rest/caregiver")
    public Map<String, Object> getRestCalendarForm(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("startTime") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam("endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime) {

        // 일정 카테고리 설정 (휴식 일정 카테고리 설정)
        Calendar calendar = new Calendar();
        calendar.setCategory("rest");

        // 해당 날짜의 모든 일정 조회 (돌보미가 있는/없는 일정 포함)
        List<Calendar> allCalendars = calendarRepository.findByDate(date);

        // 모든 CareAssignment 목록 조회 (모든 돌보미 목록)
        List<CareAssignment> allCareAssignments = careAssignmentRepository.findAll();

        // 해당 날짜와 시간에 이미 일정이 있는 돌보미 ID를 구하기
        Set<Long> busyCaregiverIds = allCalendars.stream()
                .filter(cal -> cal.getCareAssignment() != null && isScheduleConflict(cal.getStartTime(), cal.getEndTime(), startTime, endTime)) // 일정이 겹치는 경우만
                .map(cal -> cal.getCareAssignment().getId()) // 해당 돌보미의 ID를 추출
                .collect(Collectors.toSet()); // 중복 제거

        // 모든 CareAssignment 중에서 일정이 겹치지 않는 돌보미만 available = true로 설정
        List<CareAssignment> availableAssignments = allCareAssignments.stream()
                .map(assignment -> {
                    // 일정이 겹치는 돌보미는 available = false, 아니면 true
                    boolean isAvailable = !busyCaregiverIds.contains(assignment.getId());
                    assignment.setAvailable(isAvailable);
                    return assignment;
                })
                .collect(Collectors.toList());

        // 'calendar' 객체에 설정된 값을 그대로 사용
        calendar.setCareAssignments(availableAssignments);  // 해당 시간대에 가능한 돌보미 목록 설정

        // 응답 객체 준비
        Map<String, Object> response = new HashMap<>();
        response.put("calendar", calendar);  // calendar 객체를 그대로 반환
        response.put("restType", getRestTypes());  // restType 반환 (미리 설정된 리스트)

        return response;
    }

    // 일정 충돌 여부 확인
    // 일정 충돌 여부 확인
    private boolean isScheduleConflict(LocalTime scheduleStartTime, LocalTime scheduleEndTime, LocalTime requestStartTime, LocalTime requestEndTime) {
        // 기존 일정이 요청 시작 시간과 겹치지 않거나, 기존 일정 끝 시간이 요청 종료 시간과 겹치지 않으면 충돌하지 않음
        return !(scheduleEndTime.equals(requestStartTime) || scheduleStartTime.equals(requestEndTime) || scheduleEndTime.isBefore(requestStartTime) || scheduleStartTime.isAfter(requestEndTime));
    }


    // restType 값을 반환하는 메서드
    private List<String> getRestTypes() {
        List<String> restTypes = new ArrayList<>();
        for (RestType type : RestType.values()) {
            restTypes.add(type.name());
        }
        return restTypes;
    }





    //돌봄일정 작성 폼(rest)
    @GetMapping("/rest")
    public Map<String, Object> getRestCalendarForm() {
        Calendar calendar = new Calendar();
        // DB에서 모든 CareAssignment 가져오기
        List<CareAssignment> allAssignments = careAssignmentRepository.findAll();
        calendar.setCareAssignments(allAssignments);  // 리스트 설정
        // 'available' 값이 false인 경우 강제로 true로 설정
        for (CareAssignment assignment : allAssignments) {
            if (!assignment.isAvailable()) { // available 값이 false인 경우
                assignment.setAvailable(true); // true로 강제 설정
            }
        }
        // RestType의 모든 값을 반환
        List<String> restTypes = new ArrayList<>();
        for (RestType type : RestType.values()) {
            restTypes.add(type.name());  // RestType enum 값들을 리스트로 저장
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
            assignmentDetails.put("calendar", assignment.getCalendar());  // 필요에 따라 설정

            careAssignmentsWithDetails.add(assignmentDetails);
        }

        // 최종 응답 객체에 추가
        response.put("calendar", calendar);
        response.put("restType", restTypes);  // RestType 리스트 추가

        return response;
    }
    @GetMapping("/hospital/caregiver")
    public Map<String, Object> getHospitalCalendarForm(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("startTime") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam("endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime) {

        Calendar calendar = new Calendar();

        // 특정 날짜와 시간대에 일정이 있는 CareAssignment ID 가져오기
        List<Long> busyCaregiverIds = calendarRepository.findByDate(date).stream()
                .filter(cal -> !(cal.getEndTime().isBefore(startTime) || cal.getStartTime().isAfter(endTime))) // 일정 겹침 확인
                .map(cal -> cal.getCareAssignment().getId())
                .distinct()
                .toList();

        // 모든 CareAssignment 중에서 일정이 없는 돌보미 필터링
        List<CareAssignment> availableAssignments = careAssignmentRepository.findAll().stream()
                .filter(assignment -> !busyCaregiverIds.contains(assignment.getId()))
                .toList();

        calendar.setCareAssignments(availableAssignments); // 필터링된 돌보미 리스트 설정

        // RestType의 모든 값을 반환
        List<String> restTypes = new ArrayList<>();
        for (RestType type : RestType.values()) {
            restTypes.add(type.name()); // RestType enum 값들을 리스트로 저장
        }

        // TransportationTpye의 모든 값을 반환
        List<String> transportationTypes = new ArrayList<>();
        for (TransportationTpye type : TransportationTpye.values()) {
            transportationTypes.add(type.name()); // TransportationTpye enum 값들을 리스트로 저장
        }

        // category는 "hospital"로 고정
        calendar.setCategory("hospital");

        // 응답으로 반환할 데이터 준비
        Map<String, Object> response = new HashMap<>();

        // careAssignments 내부에 돌보미 정보 포함
        List<Map<String, Object>> careAssignmentsWithDetails = new ArrayList<>();
        for (CareAssignment assignment : availableAssignments) {
            Map<String, Object> assignmentDetails = new HashMap<>();
            assignmentDetails.put("id", assignment.getId());

            // 돌보미 정보
            Map<String, Object> memberDetails = new HashMap<>();
            memberDetails.put("id", assignment.getMember().getId());
            memberDetails.put("alias", assignment.getMember().getAlias());
            memberDetails.put("email", assignment.getMember().getEmail());

            assignmentDetails.put("member", memberDetails);
            assignmentDetails.put("relationship", assignment.getRelationship());
            assignmentDetails.put("calendar", assignment.getCalendar());  // 필요에 따라 설정

            careAssignmentsWithDetails.add(assignmentDetails);
        }

        // 최종 응답 객체에 추가
        response.put("calendar", calendar);
        response.put("careAssignments", careAssignmentsWithDetails);  // 필터링된 CareAssignment 리스트
        response.put("restType", restTypes);  // RestType 리스트 추가
        response.put("transportationType", transportationTypes);  // TransportationTpye 리스트 추가

        return response;
    }

    //병원폼
    @GetMapping("/hospital")
    public Map<String, Object> getHospitalCalendarForm() {
        Calendar calendar = new Calendar();
        // DB에서 모든 CareAssignment 가져오기
        List<CareAssignment> allAssignments = careAssignmentRepository.findAll();
        calendar.setCareAssignments(allAssignments);  // 리스트 설정

        // RestType의 모든 값을 반환
        List<String> restTypes = new ArrayList<>();
        for (RestType type : RestType.values()) {
            restTypes.add(type.name());  // RestType enum 값들을 리스트로 저장
        }
        List<String> transportationTypes = new ArrayList<>();
        for (TransportationTpye type : TransportationTpye.values()) {
            transportationTypes.add(type.name());  // RestType enum 값들을 리스트로 저장
        }

        // category는 "rest"로 고정
        calendar.setCategory("hospital");

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
            assignmentDetails.put("calendar", assignment.getCalendar());  // 필요에 따라 설정

            careAssignmentsWithDetails.add(assignmentDetails);
        }

        // 최종 응답 객체에 추가
        response.put("calendar", calendar);
        response.put("transportationType", transportationTypes);  // RestType 리스트 추가

        return response;
    }
    @GetMapping("/meal/caregiver")
    public Map<String, Object> getMealCalendarForm(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("startTime") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam("endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime) {

        Calendar calendar = new Calendar();

        // 특정 날짜와 시간대에 일정이 있는 CareAssignment ID 가져오기
        List<Long> busyCaregiverIds = calendarRepository.findByDate(date).stream()
                .filter(cal -> !(cal.getEndTime().isBefore(startTime) || cal.getStartTime().isAfter(endTime))) // 일정 겹침 확인
                .map(cal -> cal.getCareAssignment().getId())
                .distinct()
                .toList();

        // 모든 CareAssignment 중에서 일정이 없는 돌보미 필터링
        List<CareAssignment> availableAssignments = careAssignmentRepository.findAll().stream()
                .filter(assignment -> !busyCaregiverIds.contains(assignment.getId()))
                .toList();

        calendar.setCareAssignments(availableAssignments); // 필터링된 돌보미 리스트 설정

        // MealType의 모든 값을 반환
        List<String> mealTypes = new ArrayList<>();
        for (MealType type : MealType.values()) {
            mealTypes.add(type.name()); // MealType enum 값들을 리스트로 저장
        }

        // category는 "meal"로 고정
        calendar.setCategory("meal");

        // 응답으로 반환할 데이터 준비
        Map<String, Object> response = new HashMap<>();

        // careAssignments 내부에 돌보미 정보 포함
        List<Map<String, Object>> careAssignmentsWithDetails = new ArrayList<>();
        for (CareAssignment assignment : availableAssignments) {
            Map<String, Object> assignmentDetails = new HashMap<>();
            assignmentDetails.put("id", assignment.getId());

            // 돌보미 정보
            Map<String, Object> memberDetails = new HashMap<>();
            memberDetails.put("id", assignment.getMember().getId());
            memberDetails.put("alias", assignment.getMember().getAlias());
            memberDetails.put("email", assignment.getMember().getEmail());

            assignmentDetails.put("member", memberDetails);
            assignmentDetails.put("relationship", assignment.getRelationship());
            assignmentDetails.put("calendar", assignment.getCalendar());  // 필요에 따라 설정

            careAssignmentsWithDetails.add(assignmentDetails);
        }

        // 최종 응답 객체에 추가
        response.put("calendar", calendar);
        response.put("careAssignments", careAssignmentsWithDetails);  // 필터링된 CareAssignment 리스트
        response.put("mealType", mealTypes);  // MealType 리스트 추가

        return response;
    }

    @GetMapping("/meal")
    public Map<String, Object> getMealCalendarForm() {
        Calendar calendar = new Calendar();
        // DB에서 모든 CareAssignment 가져오기
        List<CareAssignment> allAssignments = careAssignmentRepository.findAll();
        calendar.setCareAssignments(allAssignments);  // 리스트 설정

        // RestType의 모든 값을 반환
        List<String> mealTypes = new ArrayList<>();
        for (MealType type : MealType.values()) {
            mealTypes.add(type.name());  // RestType enum 값들을 리스트로 저장
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
            assignmentDetails.put("calendar", assignment.getCalendar());  // 필요에 따라 설정

            careAssignmentsWithDetails.add(assignmentDetails);
        }

        // 최종 응답 객체에 추가
        response.put("calendar", calendar);
        response.put("mealType", mealTypes);  // RestType 리스트 추가

        return response;
    }
    @GetMapping("/others")
    public Map<String, Object> getOtherCalendarForm() {
        Calendar calendar = new Calendar();

        // DB에서 모든 CareAssignment 가져오기
        List<CareAssignment> allAssignments = careAssignmentRepository.findAll();
        calendar.setCareAssignments(allAssignments);  // 리스트 설정
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
            assignmentDetails.put("calendar", assignment.getCalendar());  // 필요에 따라 설정

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
    @GetMapping("/others/caregiver")
    public Map<String, Object> getOtherCalendarForm(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("startTime") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam("endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime) {

        Calendar calendar = new Calendar();

        // 특정 날짜와 시간대에 일정이 있는 CareAssignment ID 가져오기
        List<Long> busyCaregiverIds = calendarRepository.findByDate(date).stream()
                .filter(cal -> !(cal.getEndTime().isBefore(startTime) || cal.getStartTime().isAfter(endTime))) // 일정 겹침 확인
                .map(cal -> cal.getCareAssignment().getId())
                .distinct()
                .toList();

        // 모든 CareAssignment 중에서 일정이 없는 돌보미 필터링
        List<CareAssignment> availableAssignments = careAssignmentRepository.findAll().stream()
                .filter(assignment -> !busyCaregiverIds.contains(assignment.getId()))
                .toList();

        calendar.setCareAssignments(availableAssignments); // 필터링된 돌보미 리스트 설정
        calendar.setCategory("others");

        // 응답으로 반환할 데이터 준비
        Map<String, Object> response = new HashMap<>();

        // careAssignments 내부에 돌보미 정보 포함
        List<Map<String, Object>> careAssignmentsWithDetails = new ArrayList<>();
        for (CareAssignment assignment : availableAssignments) {
            Map<String, Object> assignmentDetails = new HashMap<>();
            assignmentDetails.put("id", assignment.getId());

            // 돌보미 정보
            Map<String, Object> memberDetails = new HashMap<>();
            memberDetails.put("id", assignment.getMember().getId());
            memberDetails.put("alias", assignment.getMember().getAlias());
            memberDetails.put("email", assignment.getMember().getEmail());

            assignmentDetails.put("member", memberDetails);
            assignmentDetails.put("relationship", assignment.getRelationship());
            assignmentDetails.put("calendar", assignment.getCalendar());  // 필요에 따라 설정

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
        response.put("careAssignments", careAssignmentsWithDetails);  // 필터링된 CareAssignment 리스트

        return response;
    }

    @GetMapping("/medication")
    public Map<String, Object> getMedicationCalendarForm() {
        Calendar calendar = new Calendar();

        // DB에서 모든 CareAssignment 가져오기
        List<CareAssignment> allAssignments = careAssignmentRepository.findAll();
        calendar.setCareAssignments(allAssignments);  // 리스트 설정
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
            assignmentDetails.put("calendar", assignment.getCalendar());  // 필요에 따라 설정

            careAssignmentsWithDetails.add(assignmentDetails);
        }

        // othersType 데이터를 가져와서 응답에 추가
        List<String> medicationTypeList = new ArrayList<>();
        for (Medication medication : calendar.getMedications()) {
            medicationTypeList.add(medication.getMedicationType()); // Others 엔티티에서 'othersType' 값을 리스트에 추가
        }

        // 최종 응답 객체에 추가
        response.put("calendar", calendar);
        response.put("medicationType", medicationTypeList);  // othersType 리스트를 'others'로 반환

        return response;
    }
    @GetMapping("/medication/caregiver")
    public Map<String, Object> getMedicationCalendarForm(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("startTime") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam("endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime) {

        Calendar calendar = new Calendar();

        // 특정 날짜와 시간대에 일정이 있는 CareAssignment ID 가져오기
        List<Long> busyCaregiverIds = calendarRepository.findByDate(date).stream()
                .filter(cal -> !(cal.getEndTime().isBefore(startTime) || cal.getStartTime().isAfter(endTime))) // 일정 겹침 확인
                .map(cal -> cal.getCareAssignment().getId())
                .distinct()
                .toList();

        // 모든 CareAssignment 중에서 일정이 없는 돌보미 필터링
        List<CareAssignment> availableAssignments = careAssignmentRepository.findAll().stream()
                .filter(assignment -> !busyCaregiverIds.contains(assignment.getId()))
                .toList();

        calendar.setCareAssignments(availableAssignments); // 필터링된 돌보미 리스트 설정
        calendar.setCategory("medication");

        // 응답으로 반환할 데이터 준비
        Map<String, Object> response = new HashMap<>();

        // careAssignments 내부에 돌보미 정보 포함
        List<Map<String, Object>> careAssignmentsWithDetails = new ArrayList<>();
        for (CareAssignment assignment : availableAssignments) {
            Map<String, Object> assignmentDetails = new HashMap<>();
            assignmentDetails.put("id", assignment.getId());

            // 돌보미 정보
            Map<String, Object> memberDetails = new HashMap<>();
            memberDetails.put("id", assignment.getMember().getId());
            memberDetails.put("alias", assignment.getMember().getAlias());
            memberDetails.put("email", assignment.getMember().getEmail());

            assignmentDetails.put("member", memberDetails);
            assignmentDetails.put("relationship", assignment.getRelationship());
            assignmentDetails.put("calendar", assignment.getCalendar());  // 필요에 따라 설정

            careAssignmentsWithDetails.add(assignmentDetails);
        }

        // medicationType 데이터를 가져와서 응답에 추가
        List<String> medicationTypeList = new ArrayList<>();
        for (Medication medication : calendar.getMedications()) {
            medicationTypeList.add(medication.getMedicationType()); // Medication 엔티티에서 'medicationType' 값을 리스트에 추가
        }

        // 최종 응답 객체에 추가
        response.put("calendar", calendar);
        response.put("medicationType", medicationTypeList);  // medicationType 리스트를 반환
        response.put("careAssignments", careAssignmentsWithDetails);  // 필터링된 CareAssignment 리스트

        return response;
    }

    //휴식돌봄일정
    @PostMapping("/rest")
    public Calendar createRestCalendar(@RequestBody Calendar calendar) {
        Long careAssignmentId = calendar.getCareAssignmentId(); // careAssignmentId 추출
        System.out.println("careAssignmentId: " + careAssignmentId);  // 로그 추가

        // careAssignmentId가 null이면 findById 호출하지 않음
        CareAssignment careAssignment = null;
        if (careAssignmentId != null) {
            careAssignment = careAssignmentRepository.findById(careAssignmentId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 돌보미를 찾을 수 없습니다: " + careAssignmentId));
        }
        if (calendar.getIsShared() == null) {
            calendar.setIsShared(true);  // true로 설정
        }

        if (calendar.getCategory() == null || calendar.getCategory().isEmpty()) {
            calendar.setCategory("rest");
        }

        // CareAssignment를 Calendar에 설정
        calendar.setCareAssignment(careAssignment);

        // Calendar 저장
        return careCalendarService.createCalendarForAssignment(careAssignmentId, calendar);

    }

    @PostMapping("/hospital")
    public Calendar createHospitalCalendar(@RequestBody Calendar calendar) {
        Long careAssignmentId = calendar.getCareAssignmentId(); // careAssignmentId 추출
        System.out.println("careAssignmentId: " + careAssignmentId);  // 로그 추가

        // careAssignmentId가 null이면 findById 호출하지 않음
        CareAssignment careAssignment = null;
        if (careAssignmentId != null) {
            careAssignment = careAssignmentRepository.findById(careAssignmentId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 돌보미를 찾을 수 없습니다: " + careAssignmentId));
        }
        if (calendar.getIsShared() == null) {
            calendar.setIsShared(true);  // true로 설정
        }

        // CareAssignment를 Calendar에 설정
        calendar.setCareAssignment(careAssignment);
        if (calendar.getCategory() == null || calendar.getCategory().isEmpty()) {
            calendar.setCategory("hospital");
        }
        // Calendar 저장
        return careCalendarService.createCalendarForAssignment(careAssignmentId, calendar);
    }
    @PostMapping("/meal")
    public Calendar createMealCalendar(@RequestBody Calendar calendar) {
        Long careAssignmentId = calendar.getCareAssignmentId(); // careAssignmentId 추출
        System.out.println("careAssignmentId: " + careAssignmentId);  // 로그 추가

        // careAssignmentId가 null이면 findById 호출하지 않음
        CareAssignment careAssignment = null;
        if (careAssignmentId != null) {
            careAssignment = careAssignmentRepository.findById(careAssignmentId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 돌보미를 찾을 수 없습니다: " + careAssignmentId));
        }
        if (calendar.getIsShared() == null) {
            calendar.setIsShared(true);  // true로 설정
        }

        if (calendar.getCategory() == null || calendar.getCategory().isEmpty()) {
            calendar.setCategory("meal");
        }
        // CareAssignment를 Calendar에 설정
        calendar.setCareAssignment(careAssignment);

        // Calendar 저장
        return careCalendarService.createCalendarForAssignment(careAssignmentId, calendar);
    }
    @PostMapping("/others")
    public Calendar createOthersCalendar(@RequestBody Calendar calendar) {
        Long careAssignmentId = calendar.getCareAssignmentId(); // careAssignmentId 추출
        System.out.println("careAssignmentId: " + careAssignmentId);  // 로그 추가

        // careAssignmentId가 null이면 findById 호출하지 않음
        CareAssignment careAssignment = null;
        if (careAssignmentId != null) {
            careAssignment = careAssignmentRepository.findById(careAssignmentId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 돌보미를 찾을 수 없습니다: " + careAssignmentId));
        }
        if (calendar.getIsShared() == null) {
            calendar.setIsShared(true);  // true로 설정
        }

        if (calendar.getCategory() == null || calendar.getCategory().isEmpty()) {
            calendar.setCategory("others");
        }
        // CareAssignment를 Calendar에 설정
        calendar.setCareAssignment(careAssignment);

        // Calendar 저장
        return careCalendarService.createCalendarForAssignment(careAssignmentId, calendar);
    }
    @PostMapping("/medication")
    public Calendar createMedicationCalendar(@RequestBody Calendar calendar) {
        Long careAssignmentId = calendar.getCareAssignmentId(); // careAssignmentId 추출
        System.out.println("careAssignmentId: " + careAssignmentId);  // 로그 추가

        // careAssignmentId가 null이면 findById 호출하지 않음
        CareAssignment careAssignment = null;
        if (careAssignmentId != null) {
            careAssignment = careAssignmentRepository.findById(careAssignmentId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 돌보미를 찾을 수 없습니다: " + careAssignmentId));
        }
        if (calendar.getIsShared() == null) {
            calendar.setIsShared(true);  // true로 설정
        }

        if (calendar.getCategory() == null || calendar.getCategory().isEmpty()) {
            calendar.setCategory("medication");
        }
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

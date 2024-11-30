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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/careCalendar")
public class CareCalendarController {

    @Autowired
    private CareAssignmentRepository careAssignmentRepository;
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

    //돌봄일정 작성 폼(rest)
    @GetMapping("/rest")
    public Map<String, Object> getRestCalendarForm() {
        Calendar calendar = new Calendar();
        // DB에서 모든 CareAssignment 가져오기
        List<CareAssignment> allAssignments = careAssignmentRepository.findAll();
        calendar.setCareAssignments(allAssignments);  // 리스트 설정

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
    //휴식돌봄일정
    @PostMapping("/rest")
    public Calendar createRestCalendar(@RequestBody Calendar calendar) {
        Long careAssignmentId = calendar.getCareAssignmentId(); // careAssignmentId 추출

//        if (careAssignmentId == null) {
//            throw new IllegalArgumentException("돌보미가 없어요");
//        }

        // CareAssignment 조회
        CareAssignment careAssignment = careAssignmentRepository.findById(careAssignmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 돌보미를 찾을 수 없습니다: " + careAssignmentId));
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

//        if (careAssignmentId == null) {
//            throw new IllegalArgumentException("돌보미가 없어요");
//        }

        // CareAssignment 조회
        CareAssignment careAssignment = careAssignmentRepository.findById(careAssignmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 돌보미를 찾을 수 없습니다: " + careAssignmentId));
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

//        if (careAssignmentId == null) {
//            throw new IllegalArgumentException("돌보미가 없어요");
//        }
        if (calendar.getIsShared() == null) {
            calendar.setIsShared(true);  // true로 설정
        }

        // CareAssignment 조회
        CareAssignment careAssignment = careAssignmentRepository.findById(careAssignmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 돌보미를 찾을 수 없습니다: " + careAssignmentId));
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

//        if (careAssignmentId == null) {
//            throw new IllegalArgumentException("돌보미가 없어요");
//        }
        if (calendar.getIsShared() == null) {
            calendar.setIsShared(true);  // true로 설정
        }

        // CareAssignment 조회
        CareAssignment careAssignment = careAssignmentRepository.findById(careAssignmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 돌보미를 찾을 수 없습니다: " + careAssignmentId));
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

//        if (careAssignmentId == null) {
//            throw new IllegalArgumentException("돌보미가 없어요");
//        }
        if (calendar.getIsShared() == null) {
            calendar.setIsShared(true);  // true로 설정
        }

        // CareAssignment 조회
        CareAssignment careAssignment = careAssignmentRepository.findById(careAssignmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 돌보미를 찾을 수 없습니다: " + careAssignmentId));
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

package com.example._2024_danpoong_team_39_be.calendar;

import com.example._2024_danpoong_team_39_be.domain.CareAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CalendarRepository extends JpaRepository<Calendar, Long> {

    List<Calendar> findByCareAssignmentId(Long careAssignmentId);
    // isShared가 true인 일정만 날짜별로 조회
    List<Calendar> findByDateAndIsSharedTrue(LocalDate date);

    // isShared가 true이고 startDate가 특정 주의 날짜 리스트에 포함된 일정 조회
    List<Calendar> findByDateInAndIsSharedTrue(List<LocalDate> weekDates);

    // isShared가 true인 모든 일정 조회
    List<Calendar> findByIsSharedTrue();

    // CareAssignmentId로 Calendar를 조회하는 메서드
    List<Calendar> findCalendarByCareAssignmentId(Long careAssignmentId);

    List<Calendar> findCalendarByCareAssignmentIdAndDate(Long careAssignmentId, LocalDate date);

    List<Calendar> findByDateAndId(LocalDate date, Long id);

    List<Calendar> findAllByCareAssignmentAndRepeatCycle(CareAssignment careAssignment, Calendar.RepeatCycle repeatCycle);

//    List<Calendar> findByDateAndStartTimeLessThanAndEndTimeGreaterThan(LocalDate date, LocalTime adjustedEndTime, LocalTime adjustedStartTime);
//    @Query("SELECT ca FROM CareAssignment ca " +
//            "JOIN ca.calendars c " +
//            "WHERE c.date = :date " +
//            "AND (c.startTime BETWEEN :startTime AND :endTime " +
//            "OR c.endTime BETWEEN :startTime AND :endTime)")
//    List<CareAssignment> findCareAssignmentsByDateAndTimeRange(@Param("date") LocalDate date,
//                                                               @Param("startTime") LocalTime startTime,
//                                                               @Param("endTime") LocalTime endTime);
}

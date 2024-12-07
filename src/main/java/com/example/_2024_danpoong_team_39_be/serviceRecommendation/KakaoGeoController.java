package com.example._2024_danpoong_team_39_be.serviceRecommendation;

import com.example._2024_danpoong_team_39_be.careAssignment.CareAssignmentService;
import com.example._2024_danpoong_team_39_be.careCalendar.CareCalendarService;
import com.example._2024_danpoong_team_39_be.domain.CareRecipient;
import com.example._2024_danpoong_team_39_be.login.repository.CareRecipientRepository;
import com.example._2024_danpoong_team_39_be.login.service.CareRecipentService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;

@RestController
@RequestMapping("/api")
public class KakaoGeoController {
    @Autowired
    private KakaoGeoService kakaoGeoService;

    // careRecipient 객체에서 주소를 가져와서 추천 장소를 찾는 API
    @GetMapping("/recommendation/{careRecipientId}")
    public ResponseEntity<String> searchHospital(@PathVariable Long careRecipientId,
                                                 @RequestParam LocalDate date,
                                                 @RequestParam LocalTime startTime,
                                                 @RequestParam LocalTime endTime) {
        String responseMessage = kakaoGeoService.getCareAssignmentDetails(careRecipientId, date, startTime, endTime);
        return ResponseEntity.ok(responseMessage);
    }

}

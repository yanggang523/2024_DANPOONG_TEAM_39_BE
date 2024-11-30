package com.example._2024_danpoong_team_39_be.serviceRecommendation;

import com.example._2024_danpoong_team_39_be.domain.CareRecipient;
import com.example._2024_danpoong_team_39_be.login.repository.CareRecipientRepository;
import com.example._2024_danpoong_team_39_be.login.service.CareRecipentService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequestMapping("/api")
public class KakaoGeoController {
    @Autowired
    private KakaoGeoService kakaoGeoService;

    // careRecipientService나 repository를 통해 careRecipient를 가져옵니다.
    @Autowired
    private CareRecipentService careRecipentService;
    @Autowired
    private CareRecipientRepository careRecipientRepository;

    private final Random random = new Random();
    @Value("${kakao.api.key}")
    private String apiKey;
    // careRecipient 객체에서 주소를 가져와서 추천 장소를 찾는 API
    @GetMapping("/recommendation/{careRecipientId}")
    public ResponseEntity<String> searchHospital(@PathVariable Long careRecipientId, HttpServletResponse response) {
        // careRecipientId로 careRecipient 조회
        CareRecipient careRecipient = careRecipientRepository.findById(careRecipientId)
                .orElseThrow(() -> new IllegalArgumentException("해당 careRecipient을 찾을 수 없습니다."));

        // careRecipient 객체에서 주소 추출
        String address = careRecipient.getAddress();
        System.out.println(address);
        // 주소로부터 위도/경도 변환
        String coordinates = kakaoGeoService.getCoordinatesFromAddress(address);

        if (coordinates.equals("주소에 대한 좌표를 찾을 수 없습니다.")) {
            return ResponseEntity.badRequest().body("주소를 찾을 수 없습니다.");
        }

        // 위도와 경도 분리
        String[] coord = coordinates.split(",");
        double x = Double.parseDouble(coord[1]); // 경도
        double y = Double.parseDouble(coord[0]); // 위도

        // 쿼리 배열
        String[] queries = {"데이케어 센터", "간병인", "방문요양"};
        int radius = 2000;
        // 배열 중 하나를 랜덤으로 픽
        String query = queries[random.nextInt(queries.length)];

        // 장소 검색
        String result = kakaoGeoService.searchPlaces(query, x, y, radius);
        response.setHeader("Authorization", "KakaoAK " + apiKey);
        return ResponseEntity.ok(result);
    }
}

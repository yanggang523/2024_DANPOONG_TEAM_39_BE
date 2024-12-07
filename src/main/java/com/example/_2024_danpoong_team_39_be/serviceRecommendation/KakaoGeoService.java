package com.example._2024_danpoong_team_39_be.serviceRecommendation;

import com.example._2024_danpoong_team_39_be.careCalendar.CareCalendarService;
import com.example._2024_danpoong_team_39_be.domain.CareRecipient;
import com.example._2024_danpoong_team_39_be.login.repository.CareRecipientRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;

@Service
public class KakaoGeoService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${kakao.api.key}")
    private String apiKey;

    @Autowired
    private CareCalendarService careCalendarService;

    @Autowired
    private CareRecipientRepository careRecipientRepository;

    private final Random random = new Random();

    // 주소로부터 위도/경도를 얻는 메서드
    public String getCoordinatesFromAddress(String address) {
        String url = "https://dapi.kakao.com/v2/local/search/address.json";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + apiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String finalUrl = String.format("%s?query=%s", url, address);

        ResponseEntity<String> response = restTemplate.exchange(
                finalUrl,
                HttpMethod.GET,
                entity,
                String.class
        );

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());

            JsonNode documentsNode = rootNode.path("documents");
            if (documentsNode.isArray() && documentsNode.size() > 0) {
                JsonNode firstPlace = documentsNode.get(0);
                double latitude = firstPlace.path("y").asDouble();
                double longitude = firstPlace.path("x").asDouble();
                return String.format("%f,%f", latitude, longitude);
            } else {
                return "주소에 대한 좌표를 찾을 수 없습니다.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error parsing the response.";
        }
    }

    // 장소 검색 메서드
    public String searchPlaces(String query, double x, double y, int radius) {
        String url = "https://dapi.kakao.com/v2/local/search/keyword.json";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + apiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String encodedQuery = query;
        String finalUrl = String.format("%s?query=%s&x=%f&y=%f&radius=%d", url, encodedQuery, x, y, radius);

        ResponseEntity<String> response = restTemplate.exchange(
                finalUrl,
                HttpMethod.GET,
                entity,
                String.class
        );

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());

            JsonNode documentsNode = rootNode.path("documents");
            if (documentsNode.isArray() && documentsNode.size() > 0) {
                JsonNode firstPlace = documentsNode.get(0);
                return firstPlace.toString();
            } else {
                return "키워드 관련 장소를 찾지 못했습니다.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error parsing the response.";
        }
    }

    // 새로운 메서드: 돌보미 유무 확인 및 추천 장소 검색
    public String getCareAssignmentDetails(Long careRecipientId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        CareRecipient careRecipient = careRecipientRepository.findById(careRecipientId)
                .orElseThrow(() -> new IllegalArgumentException("해당 careRecipient을 찾을 수 없습니다."));

        String address = careRecipient.getAddress();
        String coordinates = getCoordinatesFromAddress(address);
        if (coordinates.equals("주소에 대한 좌표를 찾을 수 없습니다.")) {
            return "주소를 찾을 수 없습니다.";
        }

        String[] coord = coordinates.split(",");
        double x = Double.parseDouble(coord[1]);
        double y = Double.parseDouble(coord[0]);

        String[] queries = {"데이케어 센터", "간병인", "방문요양"};
        int radius = 2000;
        String query = queries[random.nextInt(queries.length)];

        String placeSearchResult = searchPlaces(query, x, y, radius);

        String availabilityMessage = careCalendarService.checkAvailableCaregiversForEmptySlot(date, startTime, endTime);

        return availabilityMessage + "\n" + placeSearchResult;
    }
}

package com.example._2024_danpoong_team_39_be.notification;

import com.example._2024_danpoong_team_39_be.calendar.Calendar;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private static Map<Long, AtomicInteger> notificationCounts = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    // 메시지 알림
    // NotificationService.java
    public SseEmitter subscribe(String token) {
        SseEmitter sseEmitter = new SseEmitter(10 * 60 * 1000L); // 타임아웃: 10분

        try {
            // 초기 연결 이벤트 전송 (포스트맨에서 확인 가능)
            Map<String, String> connectEventData = new HashMap<>();
            connectEventData.put("status", "connected");
            connectEventData.put("message", "SSE 연결이 성공적으로 이루어졌습니다.");
            sseEmitter.send(SseEmitter.event().name("connect").data(connectEventData));

            // 테스트용 임의 데이터 전송
            Map<String, String> testEventData = new HashMap<>();
            testEventData.put("notification", "이것은 테스트 알림입니다.");
            sseEmitter.send(SseEmitter.event().name("testEvent").data(testEventData));

        } catch (IOException e) {
            log.error("SSE 연결 초기화 오류 발생: {}", e.getMessage());
        }

        // SSE 연결 관리 및 콜백 설정
        NotificationController.sseEmitters.put(token, sseEmitter); // 여기에 등록됨

        sseEmitter.onCompletion(() -> {
            log.info("SSE 연결 완료: {}", token);
            NotificationController.sseEmitters.remove(token);
        });

        sseEmitter.onTimeout(() -> {
            log.warn("SSE 연결 타임아웃 발생: {}", token);
            NotificationController.sseEmitters.remove(token);
        });

        sseEmitter.onError((e) -> {
            log.error("SSE 연결 오류 발생: {}", e.getMessage());
            NotificationController.sseEmitters.remove(token);
        });

        return sseEmitter;
    }




    // 알림 보내기
    public void notifyCalendar(Calendar calendar) {
        String careAssignmentId = String.valueOf(calendar.getCareAssignmentId());
        SseEmitter sseEmitter = NotificationController.sseEmitters.get(careAssignmentId);

        if (sseEmitter != null) {
            try {
                // 이벤트 데이터 생성
                Map<String, String> eventData = new HashMap<>();
                eventData.put("message", "회원님이 배정된 시간이 있습니다.");
                eventData.put("sender", calendar.getCareAssignment().getMember().getEmail());
                eventData.put("createdAt", calendar.getRegTime().toString());
                eventData.put("startTime", calendar.getStartTime().toString());
                eventData.put("endTime", calendar.getEndTime().toString());
                eventData.put("date", calendar.getDate().toString());

                // 실제 알림 전송
                sseEmitter.send(SseEmitter.event().name("addComment").data(eventData));

                // Notification 저장 및 알림 개수 관리
                Notification notification = new Notification();
                notification.setSender(calendar.getCareAssignment().getMember().getEmail());
                notification.setCreatedAt(calendar.getRegTime());
                notification.setStartTime(calendar.getStartTime().toString());
                notification.setEndTime(calendar.getEndTime().toString());
                notification.setDate(calendar.getDate().toString());
                notificationRepository.save(notification);

                // 알림 개수 증가 및 전송
                AtomicInteger count = notificationCounts.getOrDefault(calendar.getCareAssignment().getId(), new AtomicInteger(0));
                int updatedCount = count.incrementAndGet();
                notificationCounts.put(calendar.getCareAssignment().getId(), count);

                sseEmitter.send(SseEmitter.event().name("notificationCount").data(updatedCount));

            } catch (IOException e) {
                log.error("SSE 전송 중 오류 발생: {}", e.getMessage());
                NotificationController.sseEmitters.remove(careAssignmentId);  // 해당 emitter 제거
            }
        } else {
            log.warn("해당 careAssignmentId({})에 대한 SseEmitter가 없습니다.", careAssignmentId);
        }
    }

}

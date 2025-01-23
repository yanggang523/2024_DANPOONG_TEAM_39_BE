package com.example._2024_danpoong_team_39_be.notification;

import com.example._2024_danpoong_team_39_be.calendar.CalendarService;
import com.example._2024_danpoong_team_39_be.login.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    public static Map<String, SseEmitter> sseEmitters = new ConcurrentHashMap<>();
    private final JwtUtil jwtUtil;
    private final CalendarService calendarService;

    // 메시지 알림
    @GetMapping("/api/notification/subscribe")
    public SseEmitter subscribe(@RequestHeader("Authorization") String token) {
        String email = jwtUtil.getEmailFromToken(token);

        // Subscribe the user (no immediate notification here)
        SseEmitter sseEmitter = notificationService.subscribe(email);

        return sseEmitter;
    }

//    // 알림 삭제
//    @DeleteMapping("/api/notification/delete/{id}")
//    public MsgResponseDto deleteNotification(@PathVariable Long id) throws IOException {
//        return notificationService.deleteNotification(id);
//    }

}

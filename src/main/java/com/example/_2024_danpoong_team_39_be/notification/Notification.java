package com.example._2024_danpoong_team_39_be.notification;

import com.example._2024_danpoong_team_39_be.calendar.Calendar;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sender;

    private LocalDateTime createdAt;

    private String contents;        // 채팅 메시지 내용 또는 댓글 내용

    private String message;

    private String startTime;
    private String endTime;
    private String date;

    @ManyToOne(fetch = FetchType.LAZY)
    private Calendar calendarId;
}

package com.example._2024_danpoong_team_39_be.calendar;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MyCalendar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "calendar_id")  // This will join the "Calendar" entity
    private Calendar calendar;
}

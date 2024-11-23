package com.example._2024_danpoong_team_39_be.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CalendarStrap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "care_recipient_id", nullable = false)
    private CareRecipient recipient;

    @OneToMany(mappedBy="calendarStrap", cascade = CascadeType.ALL)
    private List<Member> members = new ArrayList<>();

    public CalendarStrap(CareRecipient recipient, List<Member> members) {
        this.recipient = recipient;
        this.members = members != null ? members : new ArrayList<>();
    }

    public static CalendarStrap create(CareRecipient recipient, List<Member> members) {
        return new CalendarStrap(recipient, members);
    }

}

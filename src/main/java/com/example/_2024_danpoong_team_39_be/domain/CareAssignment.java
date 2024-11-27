package com.example._2024_danpoong_team_39_be.domain;

import com.example._2024_danpoong_team_39_be.calendar.Calendar;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
//@AllArgsConstructor
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class CareAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Calendar와의 관계 설정 (OneToMany)
    @OneToMany(mappedBy = "careAssignment", fetch = FetchType.LAZY)
    private List<Calendar> caregiver;

    @OneToOne(mappedBy = "careAssignment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private CareRecipient recipient;

    @Column(length = 50)
    private String relationship;

    // careAssignment 필드 추가
    @OneToMany(mappedBy = "careAssignment")
    private List<Calendar> calendars;  // Calendar와 연결된 필드 // Calendar와의 관계를 명시적으로 추가

    public CareAssignment(Long id, Member member, CareRecipient recipient, String relationship) {
        this.id = id;
        this.member = member;
        this.recipient = recipient;
        this.relationship = relationship;
    }

    private String email;
    public String getEmail() {
        return this.member != null ? this.member.getEmail() : null;
    }

}

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

    @OneToMany(mappedBy = "careAssignment", fetch = FetchType.LAZY)
    private List<Calendar> calendars;

    @OneToMany(mappedBy = "careAssignment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Member> members = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient", nullable = false)
    private CareRecipient recipient;

    @Column(length=50)
    private String relationship;

    public CareAssignment(Long id, List<Member> members, CareRecipient recipient, String relationship) {
        this.id = id;
        this.members = members;
        this.recipient = recipient;
        this.relationship = relationship;
    }


}

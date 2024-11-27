package com.example._2024_danpoong_team_39_be.domain;

import com.example._2024_danpoong_team_39_be.calendar.Calendar;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class CareAssignment {
    @Id
    @Column(name="care_assignment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Calendar와의 관계 설정 (OneToMany)
    @OneToMany(mappedBy = "careAssignment", fetch = FetchType.LAZY)
    private List<Calendar> caregiver;

    @JoinColumn(name = "member_id", nullable = false)
    @OneToOne(cascade = CascadeType.ALL)
    private Member member;


    @JoinColumn(name = "careRecipient_id", nullable = false)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})

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

    public static CareAssignment create(Member member, CareRecipient recipient, String relationship) {
        CareAssignment careAssignment = new CareAssignment();
        careAssignment.setMember(member);
        careAssignment.setRecipient(recipient);
        careAssignment.setRelationship(relationship);

        // 양방향 관계 설정
        member.setCareAssignment(careAssignment);

        return careAssignment;
    }



}

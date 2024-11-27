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

    @OneToMany(mappedBy = "careAssignment", fetch = FetchType.LAZY)
    private List<Calendar> calendars;

    @JoinColumn(name = "member_id", nullable = false)
    @OneToOne(cascade = CascadeType.ALL)
    private Member member;


    @JoinColumn(name = "careRecipient_id", nullable = false)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private CareRecipient recipient;

    @Column(length=50)
    private String relationship;

    public CareAssignment(Long id, Member member, CareRecipient recipient, String relationship) {
        this.id = id;
        this.member = member;
        this.recipient = recipient;
        this.relationship = relationship;
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

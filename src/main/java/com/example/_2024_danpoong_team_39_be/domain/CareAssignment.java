package com.example._2024_danpoong_team_39_be.domain;

import com.example._2024_danpoong_team_39_be.calendar.Calendar;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

@Getter
@Setter
@Entity
@AllArgsConstructor
@JsonIgnoreProperties({"recipient"})
public class CareAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "care_assignment_id")
    private Long id;

    @OneToOne

    @JoinColumn(name = "member_id", unique = true, nullable = false)
    private Member member;

    @Column(nullable = false, length = 50)
    private String email;

    @JsonIgnore  // 이 필드는 직렬화 시 무시됩니다.
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "careRecipient_id", nullable = false)
    private CareRecipient recipient;

    @Column(length = 50)
    private String relationship;

    @ManyToOne
    @JoinColumn(name = "calendar_id") // The column name in the database
    private Calendar calendar;


    // Constructor for CareAssignment creation
    public static CareAssignment create(Member member, CareRecipient recipient, String relationship) {
        CareAssignment careAssignment = new CareAssignment();
        careAssignment.setMember(member);
        careAssignment.setRecipient(recipient);
        careAssignment.setRelationship(relationship);

        careAssignment.setEmail(member.getEmail());
//        이메일도 같이저장
        // Set both sides of the bi-directional relationship
        member.setCareAssignment(careAssignment);
        return careAssignment;
    }

    public CareAssignment() {
    }

}


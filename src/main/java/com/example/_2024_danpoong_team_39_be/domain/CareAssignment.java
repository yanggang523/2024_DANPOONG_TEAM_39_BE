package com.example._2024_danpoong_team_39_be.domain;

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
    private Long care_assignment_id;

    @OneToMany(mappedBy = "careAssignment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Member> members = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient", nullable = false)
    private CareRecipient recipient;

    @Column(length=50)
    private String relationship;

    public CareAssignment(Long care_assignment_id, List<Member> members, CareRecipient recipient, String relationship) {
        this.care_assignment_id = care_assignment_id;
        this.members = members;
        this.recipient = recipient;
        this.relationship = relationship;
    }


}

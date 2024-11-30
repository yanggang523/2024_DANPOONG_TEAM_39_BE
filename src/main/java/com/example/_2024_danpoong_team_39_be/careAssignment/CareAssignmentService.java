package com.example._2024_danpoong_team_39_be.careAssignment;

import com.example._2024_danpoong_team_39_be.domain.CareAssignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CareAssignmentService {
    @Autowired
    private CareAssignmentRepository careAssignmentRepository;
    public List<CareAssignment> getAllCareAssignments() {
        return careAssignmentRepository.findAll();  // DB에서 모든 CareAssignment 객체를 가져옴
    }
}

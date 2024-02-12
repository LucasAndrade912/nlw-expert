package com.example.nlwexpert.modules.certifications.useCases;

import com.example.nlwexpert.modules.students.entities.CertificationStudentEntity;
import com.example.nlwexpert.modules.students.repositories.CertificationStudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Top10RankingUseCase {
    @Autowired
    private CertificationStudentRepository certificationStudentRepository;

    public List<CertificationStudentEntity> execute() {
        return this.certificationStudentRepository.findTop10ByOrderByGrateDesc();
    }
}

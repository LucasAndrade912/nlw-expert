package com.example.nlwexpert.modules.students.useCases;

import com.example.nlwexpert.modules.students.dto.VerifyHasCertificationDTO;
import org.springframework.stereotype.Service;

@Service
public class VerifyIfHasCertificationUseCase {
    public boolean execute(VerifyHasCertificationDTO dto) {
        return dto.getEmail().equals("lucasandrade@gmail.com") && dto.getTechnology().equals("JAVA");
    }
}

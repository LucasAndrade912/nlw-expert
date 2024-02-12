package com.example.nlwexpert.modules.students.useCases;

import com.example.nlwexpert.modules.questions.repositories.QuestionRepository;
import com.example.nlwexpert.modules.students.dto.StudentCertificationAnswerDTO;
import com.example.nlwexpert.modules.students.dto.VerifyHasCertificationDTO;
import com.example.nlwexpert.modules.students.entities.AnswerCertificationEntity;
import com.example.nlwexpert.modules.students.entities.CertificationStudentEntity;
import com.example.nlwexpert.modules.students.entities.StudentEntity;
import com.example.nlwexpert.modules.students.repositories.CertificationStudentRepository;
import com.example.nlwexpert.modules.students.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class StudentCertificationAnswerUseCase {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CertificationStudentRepository certificationStudentRepository;

    @Autowired
    private VerifyIfHasCertificationUseCase verifyIfHasCertificationUseCase;

    public CertificationStudentEntity execute(StudentCertificationAnswerDTO dto) throws Exception {
        var hasCertification = this.verifyIfHasCertificationUseCase.execute(new VerifyHasCertificationDTO(dto.getEmail(), dto.getTechnology()));

        if (hasCertification) throw new Exception("Você já tirou sua certificação.");

        var questionsEntity = questionRepository.findByTechnology(dto.getTechnology());
        List<AnswerCertificationEntity> answersCertification = new ArrayList<>();

        AtomicInteger correctAnswers = new AtomicInteger(0);

        dto.getQuestionsAnswers().stream().forEach(questionAnswer -> {
            var question = questionsEntity.stream().filter(q -> q.getId().equals(questionAnswer.getQuestionID()))
                    .findFirst().get();

            var findCorrectAlternative = question.getAlternatives().stream().filter(alternative -> alternative.isCorrect())
                    .findFirst().get();

            if (findCorrectAlternative.getId().equals(questionAnswer.getAlternativeID())) {
                questionAnswer.setCorrect(true);
                correctAnswers.incrementAndGet();
            } else {
                questionAnswer.setCorrect(false);
            }

            var answerCertificationEntity = AnswerCertificationEntity.builder()
                    .answerID(questionAnswer.getAlternativeID())
                    .questionID(questionAnswer.getQuestionID())
                    .isCorrect(questionAnswer.isCorrect())
                    .build();

            answersCertification.add(answerCertificationEntity);
        });

        var student = this.studentRepository.findByEmail(dto.getEmail());
        UUID studentID;

        if (student.isEmpty()) {
            var studentCreated = StudentEntity.builder().email(dto.getEmail()).build();
            studentCreated = this.studentRepository.save(studentCreated);
            studentID = studentCreated.getId();
        } else {
            studentID = student.get().getId();
        }

        CertificationStudentEntity certificationStudentEntity = CertificationStudentEntity.builder()
                .technology(dto.getTechnology())
                .studentID(studentID)
                .grate(correctAnswers.get())
                .build();

        var certificationStudentCreated = certificationStudentRepository.save(certificationStudentEntity);

        answersCertification.stream().forEach(answerCertification -> {
            answerCertification.setCertificationID(certificationStudentEntity.getId());
            answerCertification.setCertificationStudentEntity(certificationStudentEntity);
        });

        certificationStudentEntity.setAnswersCertificationEntities(answersCertification);

        certificationStudentRepository.save(certificationStudentEntity);

        return certificationStudentCreated;
    }
}

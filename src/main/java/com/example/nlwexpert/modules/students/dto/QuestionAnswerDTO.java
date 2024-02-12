package com.example.nlwexpert.modules.students.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAnswerDTO {
    private UUID questionID;
    private UUID alternativeID;
    private boolean isCorrect;
}

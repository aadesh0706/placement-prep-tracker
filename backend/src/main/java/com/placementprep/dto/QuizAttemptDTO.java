package com.placementprep.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAttemptDTO {
    private String id;
    private String quizId;
    private String quizTitle;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Integer marksObtained;
    private Integer totalMarks;
    private Double percentage;
    private String grade;
    private Map<String, Integer> categoryWiseScore;
    private List<String> weakAreas;
    private List<String> strongAreas;
    private LocalDateTime submittedAt;
}

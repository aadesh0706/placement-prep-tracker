package com.placementprep.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
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
@Document(collection = "quiz_attempts")
public class QuizAttempt {
    @Id
    private String id;
    
    private String quizId;
    private String userId;
    
    private List<UserAnswer> answers;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Integer marksObtained;
    private Integer totalMarks;
    private Integer timeTaken; // in seconds
    
    private String status; // COMPLETED, IN_PROGRESS, TIMED_OUT
    private Double percentage;
    private String grade; // A, B, C, D, F
    
    // Analysis
    private Map<String, Integer> categoryWiseScore; // category -> marks
    private List<String> weakAreas;
    private List<String> strongAreas;
    
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;
}

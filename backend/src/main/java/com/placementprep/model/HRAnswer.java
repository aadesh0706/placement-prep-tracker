package com.placementprep.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "hr_answers")
public class HRAnswer {
    @Id
    private String id;
    
    private String userId;
    private String question;
    private String userAnswer;
    
    // AI Evaluation
    private HRAnswerEvaluation evaluation;
    private Integer score; // 1-10
    private String feedback;
    private List<String> suggestions;
    private List<String> keyPoints;
    
    private String status; // PENDING, EVALUATED
    
    private LocalDateTime submittedAt;
    private LocalDateTime evaluatedAt;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class HRAnswerEvaluation {
    private Integer clarityScore;
    private Integer confidenceScore;
    private Integer relevanceScore;
    private Integer articulationScore;
    private String overallFeedback;
    private String sampleBetterAnswer;
}

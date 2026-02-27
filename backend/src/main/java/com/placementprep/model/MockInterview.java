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
@Document(collection = "mock_interviews")
public class MockInterview {
    @Id
    private String id;
    
    private String userId;
    private String type; // TECHNICAL, HR, MR, FINAL
    private String difficulty; // EASY, MEDIUM, HARD
    
    // Interview details
    private List<String> topics; // topics to be tested
    private String targetCompany;
    
    // AI-generated questions (for practice)
    private List<InterviewQuestion> questions;
    
    // User's responses
    private List<UserResponse> responses;
    
    // Evaluation
    private Map<String, Integer> evaluationScores; // communication, technical, confidence
    private Integer overallScore;
    private String overallFeedback;
    private List<String> improvements;
    private List<String> strengths;
    
    private Integer duration; // in minutes
    private String status; // NOT_STARTED, IN_PROGRESS, COMPLETED
    
    private LocalDateTime scheduledAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
}

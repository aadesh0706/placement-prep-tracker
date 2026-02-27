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
@Document(collection = "study_sessions")
public class StudySession {
    @Id
    private String id;
    
    private String userId;
    private String topic;
    private String subtopic;
    private String category; // DSA, APTITUDE, OS, DBMS, HR, TECHNICAL
    
    private String activityType; // READING, VIDEO, PRACTICE, QUIZ
    private String resourceTitle;
    private String resourceUrl;
    
    private Integer duration; // in minutes
    private String notes;
    private List<String> tags;
    
    // Productivity
    private Integer productivityScore; // 1-10
    private String mood; // GREAT, GOOD, OKAY, TIRED
    
    // Related quiz/attempt
    private String quizAttemptId;
    
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime createdAt;
}

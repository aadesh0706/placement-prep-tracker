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
@Document(collection = "quizzes")
public class Quiz {
    @Id
    private String id;
    
    private String title;
    private String description;
    private String category; // DSA, APTITUDE, OS, DBMS, HR, TECHNICAL
    private String difficulty; // EASY, MEDIUM, HARD
    private Integer timeLimit; // in minutes
    private Integer totalMarks;
    private Integer passingMarks;
    
    private List<Question> questions;
    private String createdBy; // userId of TPO/Admin
    private String batch; // applicable for batch
    
    private Boolean isActive;
    private LocalDateTime scheduledStartTime;
    private LocalDateTime scheduledEndTime;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

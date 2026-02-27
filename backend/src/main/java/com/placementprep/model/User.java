package com.placementprep.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
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
@Document(collection = "users")
public class User {
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String email;
    
    private String password;
    private String firstName;
    private String lastName;
    private String role; // STUDENT, TPO, ADMIN
    private String batch;
    private String department;
    private String enrollmentNumber;
    private String phone;
    private String profileImageUrl;
    private String resumeUrl;
    
    // Study preferences
    private List<String> targetCompanies;
    private Integer targetPackage; // in LPA
    
    // Progress tracking
    private Map<String, Integer> topicProgress; // topic -> percentage
    private Integer overallProgress;
    private Integer totalStudyHours;
    private Integer mockInterviewsCompleted;
    private Integer quizzesCompleted;
    
    // Streaks
    private Integer currentStreak;
    private Integer longestStreak;
    private LocalDateTime lastStudyDate;
    
    // AI-generated roadmap
    private String studyRoadmapId;
    private Map<String, Object> personalizedPlan;
    
    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;
    private Boolean isEmailVerified;
}

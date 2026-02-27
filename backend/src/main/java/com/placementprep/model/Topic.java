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
@Document(collection = "topics")
public class Topic {
    @Id
    private String id;
    
    private String name;
    private String category; // DSA, APTITUDE, OS, DBMS, HR, TECHNICAL
    private String description;
    private Integer difficultyLevel; // 1-5
    
    // Content
    private List<String> subtopics;
    private List<String> resources; // URLs to study material
    private List<String> practiceLinks;
    
    // Weights for placement prep
    private Double importanceWeight; // for placement
    private Integer frequencyInPlacements; // how often asked
    
    // User-specific progress (denormalized for performance)
    // This will be stored in user's topicProgress map
    
    private Boolean isActive;
    private LocalDateTime createdAt;
}

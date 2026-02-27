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
@Document(collection = "study_roadmaps")
public class StudyRoadmap {
    @Id
    private String id;
    
    private String userId;
    private String title;
    private String description;
    
    // Target companies and package
    private List<String> targetCompanies;
    private Integer targetPackage;
    private Integer currentPackage; // if placed
    
    // Time available
    private Integer weeksAvailable;
    private Integer hoursPerWeek;
    
    // AI-generated plan
    private List<RoadmapPhase> phases;
    private Map<String, Integer> topicWeights; // importance of each topic
    
    // Progress
    private Integer overallProgress;
    private String status; // ACTIVE, COMPLETED, PAUSED
    
    private LocalDateTime generatedAt;
    private LocalDateTime updatedAt;
    private LocalDateTime targetCompletionDate;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class RoadmapPhase {
    private String id;
    private String title;
    private String description;
    private Integer weekNumber;
    private List<String> topics;
    private List<StudyResource> resources;
    private List<String> milestones;
    private Integer estimatedHours;
    private Boolean isCompleted;
    private LocalDateTime completedAt;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class StudyResource {
    private String id;
    private String title;
    private String type; // VIDEO, ARTICLE, PRACTICE, QUIZ
    private String url;
    private String description;
    private Integer duration; // in minutes
    private Boolean isCompleted;
}

package com.placementprep.model;

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
public class RoadmapPhase {
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

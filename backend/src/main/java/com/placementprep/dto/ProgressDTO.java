package com.placementprep.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressDTO {
    private String oderallProgress;
    private Integer totalStudyHours;
    private Integer quizzesCompleted;
    private Integer mockInterviewsCompleted;
    private Integer currentStreak;
    private Integer longestStreak;
    private Map<String, Integer> weeklyProgress;
    private Map<String, Integer> categoryProgress;
    private List<String> weakAreas;
    private List<String> strongAreas;
    private LocalDateTime lastStudyDate;
}

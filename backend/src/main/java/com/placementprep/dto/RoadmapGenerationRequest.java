package com.placementprep.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoadmapGenerationRequest {
    private List<String> targetCompanies;
    private Integer targetPackage;
    private Integer weeksAvailable;
    private Integer hoursPerWeek;
    private List<String> weakAreas;
    private List<String> strongAreas;
    private String targetRole; // SDE, DATA_SCIENCE, etc.
}

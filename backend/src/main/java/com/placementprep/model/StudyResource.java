package com.placementprep.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyResource {
    private String id;
    private String title;
    private String type; // VIDEO, ARTICLE, PRACTICE, QUIZ
    private String url;
    private String description;
    private Integer duration; // in minutes
    private Boolean isCompleted;
}

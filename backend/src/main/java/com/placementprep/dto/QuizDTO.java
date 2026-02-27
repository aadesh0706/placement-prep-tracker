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
public class QuizDTO {
    private String id;
    private String title;
    private String description;
    private String category;
    private String difficulty;
    private Integer timeLimit;
    private Integer totalMarks;
    private Integer passingMarks;
    private Integer questionCount;
    private Boolean isActive;
}

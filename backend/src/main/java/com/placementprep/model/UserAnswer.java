package com.placementprep.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAnswer {
    private String questionId;
    private Integer selectedOptionIndex;
    private Boolean isCorrect;
    private Integer marksObtained;
    private String category;
}

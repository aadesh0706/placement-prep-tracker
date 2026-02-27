package com.placementprep.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewQuestion {
    private String id;
    private String question;
    private String category;
    private String type; // TECHNICAL, BEHAVIORAL, SITUATIONAL
    private Integer expectedDuration; // seconds
    private String sampleAnswer;
}

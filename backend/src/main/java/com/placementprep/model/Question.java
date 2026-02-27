package com.placementprep.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {
    private String id;
    private String text;
    private List<String> options;
    private Integer correctOptionIndex; // 0-3
    private Integer marks;
    private String explanation;
    private String category;
    private String difficulty;
}

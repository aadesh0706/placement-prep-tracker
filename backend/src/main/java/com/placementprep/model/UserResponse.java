package com.placementprep.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private String questionId;
    private String audioUrl; // if recorded
    private String textResponse;
    private Integer duration; // seconds
    private Map<String, Object> evaluation; // AI evaluation
}

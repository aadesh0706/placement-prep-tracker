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
public class UserDTO {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private String batch;
    private String department;
    private String enrollmentNumber;
    private String phone;
    private String profileImageUrl;
    private Map<String, Integer> topicProgress;
    private Integer overallProgress;
    private Integer totalStudyHours;
    private Integer currentStreak;
    private LocalDateTime createdAt;
}

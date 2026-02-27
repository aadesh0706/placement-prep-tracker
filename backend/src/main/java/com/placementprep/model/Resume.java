package com.placementprep.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "resumes")
public class Resume {
    @Id
    private String id;
    
    private String userId;
    private String fileName;
    private String fileUrl;
    private String fileType; // PDF, DOCX
    
    // Extracted data
    private String extractedText;
    private Map<String, Object> parsedData; // name, email, phone, skills, education, experience
    
    // AI Evaluation
    private ResumeEvaluation evaluation;
    private Integer overallScore;
    private List<String> suggestions;
    private List<String> strengths;
    private List<String> weaknesses;
    
    // ATS Analysis
    private Double atsScore;
    private List<String> missingKeywords;
    private List<String> formattingIssues;
    
    private LocalDateTime uploadedAt;
    private LocalDateTime analyzedAt;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class ResumeEvaluation {
    private Integer clarityScore;
    private Integer relevanceScore;
    private Integer impactScore;
    private Integer formattingScore;
    private String overallFeedback;
    private List<String> recommendations;
}

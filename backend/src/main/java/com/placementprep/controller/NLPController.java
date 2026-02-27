package com.placementprep.controller;

import com.placementprep.model.HRAnswer;
import com.placementprep.model.QuizAttempt;
import com.placementprep.model.Resume;
import com.placementprep.service.NLPService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/nlp")
@RequiredArgsConstructor
public class NLPController {
    
    private final NLPService nlpService;
    
    // Resume endpoints
    @PostMapping("/resume/analyze")
    public ResponseEntity<Resume> analyzeResume(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> resumeData) {
        return ResponseEntity.ok(nlpService.analyzeResume(
                userDetails.getUsername(),
                resumeData.get("fileName"),
                resume")
        ));
   Data.get("text }
    
    @GetMapping("/resume")
    public ResponseEntity<List<Resume>> getUserResumes(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(nlpService.getUserResumes(userDetails.getUsername()));
    }
    
    // HR Answer endpoints
    @PostMapping("/hr/analyze")
    public ResponseEntity<HRAnswer> analyzeHRAnswer(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> answerData) {
        return ResponseEntity.ok(nlpService.analyzeHRAnswer(
                userDetails.getUsername(),
                answerData.get("question"),
                answerData.get("answer")
        ));
    }
    
    @GetMapping("/hr")
    public ResponseEntity<List<HRAnswer>> getUserHRAnswers(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(nlpService.getUserHRAnswers(userDetails.getUsername()));
    }
    
    // ============ ENHANCED AI/ML ENDPOINTS ============
    
    /**
     * Match resume to job description
     */
    @PostMapping("/resume/match")
    public ResponseEntity<Map<String, Object>> matchResumeToJob(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> data) {
        return ResponseEntity.ok(nlpService.matchResumeToJob(
                data.get("resumeText"),
                data.get("jobDescription")
        ));
    }
    
    /**
     * Get company-specific preparation tips
     */
    @GetMapping("/company/tips")
    public ResponseEntity<Map<String, Object>> getCompanyPrepTips(
            @RequestParam String company,
            @RequestParam(required = false, defaultValue = "Software Engineer") String role) {
        return ResponseEntity.ok(nlpService.getCompanyPrepTips(company, role));
    }
    
    /**
     * Analyze weak areas from quiz performance
     */
    @GetMapping("/analysis/weak-areas")
    public ResponseEntity<Map<String, Object>> analyzeWeakAreas(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<QuizAttempt> attempts = nlpService.getUserQuizAttempts(userDetails.getUsername());
        return ResponseEntity.ok(nlpService.analyzeWeakAreas(attempts));
    }
    
    /**
     * Generate personalized study plan
     */
    @PostMapping("/study/plan")
    public ResponseEntity<Map<String, Object>> generateStudyPlan(
            @RequestBody Map<String, Object> data) {
        String goal = (String) data.get("goal");
        String level = (String) data.getOrDefault("currentLevel", "INTERMEDIATE");
        int hoursPerWeek = (Integer) data.getOrDefault("hoursPerWeek", 10);
        return ResponseEntity.ok(nlpService.generateStudyPlan(goal, level, hoursPerWeek));
    }
}

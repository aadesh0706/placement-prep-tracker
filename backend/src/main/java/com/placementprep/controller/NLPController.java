package com.placementprep.controller;

import com.placementprep.model.HRAnswer;
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
                resumeData.get("text")
        ));
    }
    
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
}

package com.placementprep.controller;

import com.placementprep.model.MockInterview;
import com.placementprep.service.MockInterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
public class MockInterviewController {
    
    private final MockInterviewService interviewService;
    
    @PostMapping
    public ResponseEntity<MockInterview> createInterview(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody MockInterview request) {
        return ResponseEntity.ok(interviewService.createInterview(userDetails.getUsername(), request));
    }
    
    @GetMapping
    public ResponseEntity<List<MockInterview>> getUserInterviews(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(interviewService.getUserInterviews(userDetails.getUsername()));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MockInterview> getInterviewById(@PathVariable String id) {
        return ResponseEntity.ok(interviewService.getInterviewById(id));
    }
    
    @PostMapping("/{id}/start")
    public ResponseEntity<MockInterview> startInterview(@PathVariable String id) {
        return ResponseEntity.ok(interviewService.startInterview(id));
    }
    
    @PostMapping("/{id}/respond")
    public ResponseEntity<MockInterview> submitResponse(
            @PathVariable String id,
            @RequestBody Map<String, String> response) {
        return ResponseEntity.ok(interviewService.submitResponse(
                id, 
                response.get("questionId"), 
                response.get("text")
        ));
    }
    
    @PostMapping("/{id}/complete")
    public ResponseEntity<MockInterview> completeInterview(
            @PathVariable String id,
            @RequestBody Map<String, Object> evaluation) {
        return ResponseEntity.ok(interviewService.completeInterview(id, evaluation));
    }
}

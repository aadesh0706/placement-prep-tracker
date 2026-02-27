package com.placementprep.controller;

import com.placementprep.dto.ProgressDTO;
import com.placementprep.model.StudySession;
import com.placementprep.service.ProgressService;
import com.placementprep.service.StudySessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {
    
    private final ProgressService progressService;
    private final StudySessionService studySessionService;
    
    @GetMapping
    public ResponseEntity<ProgressDTO> getUserProgress(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(progressService.getUserProgress(getUserId(userDetails)));
    }
    
    @GetMapping("/weekly")
    public ResponseEntity<Map<String, Object>> getWeeklyAnalytics(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(progressService.getWeeklyAnalytics(getUserId(userDetails)));
    }
    
    @PostMapping("/sessions")
    public ResponseEntity<StudySession> logStudySession(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> sessionData) {
        return ResponseEntity.ok(studySessionService.logSession(getUserId(userDetails), sessionData));
    }
    
    @GetMapping("/sessions")
    public ResponseEntity<List<StudySession>> getStudySessions(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(studySessionService.getUserSessions(getUserId(userDetails)));
    }
    
    private String getUserId(UserDetails userDetails) {
        // In production, extract from JWT
        return userDetails.getUsername();
    }
}

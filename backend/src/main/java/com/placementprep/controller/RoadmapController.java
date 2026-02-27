package com.placementprep.controller;

import com.placementprep.dto.RoadmapGenerationRequest;
import com.placementprep.model.StudyRoadmap;
import com.placementprep.service.RoadmapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roadmap")
@RequiredArgsConstructor
public class RoadmapController {
    
    private final RoadmapService roadmapService;
    
    @PostMapping("/generate")
    public ResponseEntity<StudyRoadmap> generateRoadmap(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody RoadmapGenerationRequest request) {
        return ResponseEntity.ok(roadmapService.generateRoadmap(userDetails.getUsername(), request));
    }
    
    @GetMapping
    public ResponseEntity<StudyRoadmap> getUserRoadmap(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(roadmapService.getUserRoadmap(userDetails.getUsername()));
    }
    
    @PutMapping("/phases/{phaseId}")
    public ResponseEntity<StudyRoadmap> updatePhaseProgress(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String phaseId,
            @RequestParam boolean completed) {
        StudyRoadmap roadmap = roadmapService.getUserRoadmap(userDetails.getUsername());
        return ResponseEntity.ok(roadmapService.updatePhaseProgress(roadmap.getId(), phaseId, completed));
    }
}

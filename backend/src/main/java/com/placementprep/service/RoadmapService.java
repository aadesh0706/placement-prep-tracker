package com.placementprep.service;

import com.placementprep.model.*;
import com.placementprep.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RoadmapService {
    
    private final StudyRoadmapRepository roadmapRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    
    public StudyRoadmap generateRoadmap(String userId, RoadmapGenerationRequest request) {
        // Check if roadmap already exists
        if (roadmapRepository.existsByUserId(userId)) {
            throw new RuntimeException("Roadmap already exists for this user");
        }
        
        List<RoadmapPhase> phases = new ArrayList<>();
        
        // Generate phases based on weeks available
        int totalWeeks = request.getWeeksAvailable();
        int hoursPerWeek = request.getHoursPerWeek();
        
        // Get topics from database
        List<Topic> allTopics = topicRepository.findByIsActive(true);
        
        // Group topics by category
        Map<String, List<Topic>> topicsByCategory = new HashMap<>();
        for (Topic topic : allTopics) {
            topicsByCategory.computeIfAbsent(topic.getCategory(), k -> new ArrayList<>()).add(topic);
        }
        
        // Phase 1: Foundation (Weeks 1-2)
        RoadmapPhase phase1 = createPhase(1, "Foundation", "Build strong fundamentals", 
                getTopicsByCategories(topicsByCategory, Arrays.asList("DSA", "APTITUDE")), 
                hoursPerWeek * 2, totalWeeks);
        phases.add(phase1);
        
        // Phase 2: Core Topics (Weeks 3-5)
        RoadmapPhase phase2 = createPhase(2, "Core Topics", "Deep dive into important topics",
                getTopicsByCategories(topicsByCategory, Arrays.asList("DBMS", "OS", "CN")),
                hoursPerWeek * 2, totalWeeks);
        phases.add(phase2);
        
        // Phase 3: Advanced & Interview Prep (Weeks 6-8)
        RoadmapPhase phase3 = createPhase(3, "Interview Prep", "Practice interview questions",
                getTopicsByCategories(topicsByCategory, Arrays.asList("TECHNICAL", "SYSTEM_DESIGN")),
                hoursPerWeek * 2, totalWeeks);
        phases.add(phase3);
        
        // Phase 4: Mock Interviews & Placement (Weeks 9-12)
        RoadmapPhase phase4 = createPhase(4, "Placement Ready", "Take mock interviews, apply",
                getTopicsByCategories(topicsByCategory, Arrays.asList("HR", "APTITUDE")),
                hoursPerWeek * 2, totalWeeks);
        phases.add(phase4);
        
        StudyRoadmap roadmap = StudyRoadmap.builder()
                .userId(userId)
                .title("Placement Preparation Roadmap")
                .description("Personalized AI-generated roadmap for campus placements")
                .targetCompanies(request.getTargetCompanies())
                .targetPackage(request.getTargetPackage())
                .weeksAvailable(totalWeeks)
                .hoursPerWeek(hoursPerWeek)
                .phases(phases)
                .topicWeights(calculateTopicWeights(allTopics))
                .overallProgress(0)
                .status("ACTIVE")
                .generatedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .targetCompletionDate(LocalDateTime.now().plusWeeks(totalWeeks))
                .build();
        
        // Save roadmap
        StudyRoadmap savedRoadmap = roadmapRepository.save(roadmap);
        
        // Update user with roadmap ID
        userRepository.findById(userId).ifPresent(user -> {
            user.setStudyRoadmapId(savedRoadmap.getId());
            userRepository.save(user);
        });
        
        return savedRoadmap;
    }
    
    public StudyRoadmap getUserRoadmap(String userId) {
        return roadmapRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Roadmap not found"));
    }
    
    public StudyRoadmap updatePhaseProgress(String roadmapId, String phaseId, boolean completed) {
        StudyRoadmap roadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new RuntimeException("Roadmap not found"));
        
        for (RoadmapPhase phase : roadmap.getPhases()) {
            if (phase.getId().equals(phaseId)) {
                phase.setIsCompleted(completed);
                phase.setCompletedAt(completed ? LocalDateTime.now() : null);
                break;
            }
        }
        
        // Recalculate overall progress
        int completedPhases = (int) roadmap.getPhases().stream()
                .filter(RoadmapPhase::getIsCompleted)
                .count();
        roadmap.setOverallProgress((completedPhases * 100) / roadmap.getPhases().size());
        roadmap.setUpdatedAt(LocalDateTime.now());
        
        return(LocalDateTime.now roadmapRepository.save(roadmap);
    }
    
    private RoadmapPhase createPhase(int weekNumber, String title, String description, 
                                     List<Topic> topics, int hours, int totalWeeks) {
        List<String> topicNames = topics.stream()
                .map(Topic::getName)
                .limit(10)
                .collect(java.util.stream.Collectors.toList());
        
        return RoadmapPhase.builder()
                .id(UUID.randomUUID().toString())
                .title(title)
                .description(description)
                .weekNumber(weekNumber)
                .topics(topicNames)
                .resources(new ArrayList<>())
                .milestones(Arrays.asList("Complete basics", "Pass practice quiz", "Review doubts"))
                .estimatedHours(hours)
                .isCompleted(false)
                .build();
    }
    
    private List<Topic> getTopicsByCategories(Map<String, List<Topic>> topicsByCategory, 
                                              List<String> categories) {
        List<Topic> result = new ArrayList<>();
        for (String category : categories) {
            if (topicsByCategory.containsKey(category)) {
                result.addAll(topicsByCategory.get(category));
            }
        }
        return result;
    }
    
    private Map<String, Integer> calculateTopicWeights(List<Topic> topics) {
        Map<String, Integer> weights = new HashMap<>();
        for (Topic topic : topics) {
            weights.put(topic.getName(), 
                    (int) (topic.getImportanceWeight() * 100 + topic.getFrequencyInPlacements()));
        }
        return weights;
    }
}

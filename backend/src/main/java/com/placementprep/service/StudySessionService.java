package com.placementprep.service;

import com.placementprep.model.StudySession;
import com.placementprep.model.User;
import com.placementprep.repository.StudySessionRepository;
import com.placementprep.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StudySessionService {
    
    private final StudySessionRepository sessionRepository;
    private final UserRepository userRepository;
    
    public StudySession logSession(String userId, Map<String, Object> sessionData) {
        User user = userRepository.findByEmail(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        StudySession session = StudySession.builder()
                .userId(userId)
                .topic((String) sessionData.get("topic"))
                .subtopic((String) sessionData.get("subtopic"))
                .category((String) sessionData.get("category"))
                .activityType((String) sessionData.get("activityType"))
                .resourceTitle((String) sessionData.get("resourceTitle"))
                .resourceUrl((String) sessionData.get("resourceUrl"))
                .duration((Integer) sessionData.getOrDefault("duration", 0))
                .notes((String) sessionData.get("notes"))
                .productivityScore((Integer) sessionData.get("productivityScore"))
                .mood((String) sessionData.get("mood"))
                .startedAt(LocalDateTime.now())
                .endedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
        
        StudySession saved = sessionRepository.save(session);
        
        // Update user study hours
        user.setTotalStudyHours(user.getTotalStudyHours() + session.getDuration());
        user.setLastStudyDate(LocalDateTime.now());
        userRepository.save(user);
        
        return saved;
    }
    
    public List<StudySession> getUserSessions(String userId) {
        return sessionRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}

package com.placementprep.service;

import com.placementprep.dto.ProgressDTO;
import com.placementprep.model.*;
import com.placementprep.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressService {
    
    private final UserRepository userRepository;
    private final StudySessionRepository studySessionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final MockInterviewRepository mockInterviewRepository;
    
    public ProgressDTO getUserProgress(String userId) {
        User user = userRepository.findByEmail(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<StudySession> sessions = studySessionRepository.findByUserId(userId);
        List<QuizAttempt> quizAttempts = quizAttemptRepository.findByUserId(userId);
        List<MockInterview> interviews = mockInterviewRepository.findByUserId(userId);
        
        // Calculate total study hours
        int totalMinutes = sessions.stream()
                .mapToInt(StudySession::getDuration)
                .sum();
        int totalHours = totalMinutes / 60;
        
        // Calculate weekly progress
        Map<String, Integer> weeklyProgress = calculateWeeklyProgress(sessions);
        
        // Calculate category progress
        Map<String, Integer> categoryProgress = calculateCategoryProgress(sessions, quizAttempts);
        
        // Determine weak and strong areas
        List<String> weakAreas = determineWeakAreas(quizAttempts);
        List<String> strongAreas = determineStrongAreas(quizAttempts);
        
        // Update streak
        updateStreak(user, sessions);
        
        return ProgressDTO.builder()
                .overallProgress(user.getOverallProgress())
                .totalStudyHours(totalHours)
                .quizzesCompleted(quizAttempts.size())
                .mockInterviewsCompleted((int) interviews.stream()
                        .filter(i -> "COMPLETED".equals(i.getStatus()))
                        .count())
                .currentStreak(user.getCurrentStreak())
                .longestStreak(user.getLongestStreak())
                .weeklyProgress(weeklyProgress)
                .categoryProgress(categoryProgress)
                .weakAreas(weakAreas)
                .strongAreas(strongAreas)
                .lastStudyDate(user.getLastStudyDate())
                .build();
    }
    
    public Map<String, Object> getWeeklyAnalytics(String userId) {
        List<StudySession> sessions = studySessionRepository.findByUserId(userId);
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAgo = now.minusDays(7);
        
        List<StudySession> thisWeekSessions = sessions.stream()
                .filter(s -> s.getCreatedAt().isAfter(weekAgo))
                .collect(Collectors.toList());
        
        int totalMinutes = thisWeekSessions.stream()
                .mapToInt(StudySession::getDuration)
                .sum();
        
        // Group by day
        Map<String, Integer> dailyStudyTime = new LinkedHashMap<>();
        for (int i = 6; i >= 0; i--) {
            LocalDateTime day = now.minusDays(i);
            String dayName = day.getDayOfWeek().name().substring(0, 3);
            int minutes = thisWeekSessions.stream()
                    .filter(s -> s.getCreatedAt().toLocalDate().equals(day.toLocalDate()))
                    .mapToInt(StudySession::getDuration)
                    .sum();
            dailyStudyTime.put(dayName, minutes);
        }
        
        // Most studied category
        String mostStudied = thisWeekSessions.stream()
                .collect(Collectors.groupingBy(StudySession::getCategory, Collectors.summingInt(StudySession::getDuration)))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("None");
        
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalMinutesThisWeek", totalMinutes);
        analytics.put("dailyStudyTime", dailyStudyTime);
        analytics.put("mostStudiedCategory", mostStudied);
        analytics.put("sessionsCount", thisWeekSessions.size());
        
        return analytics;
    }
    
    private Map<String, Integer> calculateWeeklyProgress(List<StudySession> sessions) {
        Map<String, Integer> weekly = new LinkedHashMap<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (int i = 3; i >= 0; i--) {
            LocalDateTime weekStart = now.minusWeeks(i);
            LocalDateTime weekEnd = (i == 0) ? now : now.minusWeeks(i - 1);
            
            int minutes = sessions.stream()
                    .filter(s -> s.getCreatedAt().isAfter(weekStart) && s.getCreatedAt().isBefore(weekEnd))
                    .mapToInt(StudySession::getDuration)
                    .sum();
            
            weekly.put("Week " + (4 - i), minutes);
        }
        
        return weekly;
    }
    
    private Map<String, Integer> calculateCategoryProgress(List<StudySession> sessions, List<QuizAttempt> attempts) {
        Map<String, Integer> progress = new HashMap<>();
        
        // Calculate from study sessions
        Map<String, Integer> studyByCategory = sessions.stream()
                .collect(Collectors.groupingBy(StudySession::getCategory, Collectors.summingInt(StudySession::getDuration)));
        
        // Calculate from quiz attempts
        Map<String, Integer> quizByCategory = new HashMap<>();
        for (QuizAttempt attempt : attempts) {
            if (attempt.getCategoryWiseScore() != null) {
                attempt.getCategoryWiseScore().forEach((k, v) -> 
                        quizByCategory.merge(k, v, Integer::sum));
            }
        }
        
        // Merge both
        Set<String> allCategories = new HashSet<>();
        allCategories.addAll(studyByCategory.keySet());
        allCategories.addAll(quizByCategory.keySet());
        
        for (String category : allCategories) {
            int studyTime = studyByCategory.getOrDefault(category, 0);
            int quizScore = quizByCategory.getOrDefault(category, 0);
            progress.put(category, studyTime + quizScore);
        }
        
        return progress;
    }
    
    private List<String> determineWeakAreas(List<QuizAttempt> attempts) {
        Map<String, List<Integer>> scoresByCategory = new HashMap<>();
        
        for (QuizAttempt attempt : attempts) {
            if (attempt.getCategoryWiseScore() != null) {
                attempt.getCategoryWiseScore().forEach((category, score) -> 
                        scoresByCategory.computeIfAbsent(category, k -> new ArrayList<>()).add(score));
            }
        }
        
        return scoresByCategory.entrySet().stream()
                .filter(e -> e.getValue().stream().mapToInt(Integer::intValue).average().orElse(0) < 60)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
    private List<String> determineStrongAreas(List<QuizAttempt> attempts) {
        Map<String, List<Integer>> scoresByCategory = new HashMap<>();
        
        for (QuizAttempt attempt : attempts) {
            if (attempt.getCategoryWiseScore() != null) {
                attempt.getCategoryWiseScore().forEach((category, score) -> 
                        scoresByCategory.computeIfAbsent(category, k -> new ArrayList<>()).add(score));
            }
        }
        
        return scoresByCategory.entrySet().stream()
                .filter(e -> e.getValue().stream().mapToInt(Integer::intValue).average().orElse(0) >= 70)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
    private void updateStreak(User user, List<StudySession> sessions) {
        if (sessions.isEmpty()) {
            return;
        }
        
        LocalDateTime lastSession = sessions.stream()
                .map(StudySession::getCreatedAt)
                .max(LocalDateTime::compareTo)
                .orElse(null);
        
        if (lastSession == null) return;
        
        LocalDateTime today = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime lastSessionDate = lastSession.toLocalDate().atStartOfDay();
        
        long daysBetween = ChronoUnit.DAYS.between(lastSessionDate, today);
        
        if (daysBetween == 0) {
            // Same day - streak continues
            user.setLastStudyDate(lastSession);
        } else if (daysBetween == 1) {
            // Yesterday - increment streak
            user.setCurrentStreak(user.getCurrentStreak() + 1);
            user.setLastStudyDate(lastSession);
            
            if (user.getCurrentStreak() > user.getLongestStreak()) {
                user.setLongestStreak(user.getCurrentStreak());
            }
        } else {
            // Streak broken
            user.setCurrentStreak(1);
            user.setLastStudyDate(lastSession);
        }
        
        userRepository.save(user);
    }
}

package com.placementprep.controller;

import com.placementprep.dto.QuizDTO;
import com.placementprep.dto.UserDTO;
import com.placementprep.model.Question;
import com.placementprep.model.Quiz;
import com.placementprep.model.Topic;
import com.placementprep.repository.QuizRepository;
import com.placementprep.repository.StudySessionRepository;
import com.placementprep.repository.TopicRepository;
import com.placementprep.repository.UserRepository;
import com.placementprep.repository.QuizAttemptRepository;
import com.placementprep.repository.MockInterviewRepository;
import com.placementprep.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final TopicRepository topicRepository;
    private final StudySessionRepository studySessionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final MockInterviewRepository mockInterviewRepository;
    private final AuthService authService;

    // ===================== PLATFORM STATS =====================
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getPlatformStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalQuizzes", quizRepository.count());
        stats.put("totalTopics", topicRepository.count());
        stats.put("totalStudySessions", studySessionRepository.count());
        stats.put("totalQuizAttempts", quizAttemptRepository.count());
        stats.put("totalMockInterviews", mockInterviewRepository.count());
        stats.put("studentCount", userRepository.findByRole("STUDENT").size());
        stats.put("tpoCount", userRepository.findByRole("TPO").size());
        return ResponseEntity.ok(stats);
    }

    // ===================== USER MANAGEMENT =====================
    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<Map<String, Object>> users = userRepository.findAll().stream().map(u -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", u.getId());
            m.put("email", u.getEmail());
            m.put("firstName", u.getFirstName());
            m.put("lastName", u.getLastName());
            m.put("role", u.getRole());
            m.put("batch", u.getBatch());
            m.put("department", u.getDepartment());
            m.put("isActive", u.getIsActive());
            m.put("overallProgress", u.getOverallProgress());
            m.put("totalStudyHours", u.getTotalStudyHours());
            m.put("createdAt", u.getCreatedAt());
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<Map<String, String>> updateUserRole(
            @PathVariable String userId,
            @RequestBody Map<String, String> body) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setRole(body.get("role"));
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        });
        return ResponseEntity.ok(Map.of("message", "Role updated"));
    }

    @PutMapping("/users/{userId}/toggle-active")
    public ResponseEntity<Map<String, Object>> toggleUserActive(@PathVariable String userId) {
        Map<String, Object> result = new HashMap<>();
        userRepository.findById(userId).ifPresent(user -> {
            user.setIsActive(!Boolean.TRUE.equals(user.getIsActive()));
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            result.put("isActive", user.getIsActive());
        });
        result.put("message", "User status updated");
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String userId) {
        userRepository.deleteById(userId);
        return ResponseEntity.ok(Map.of("message", "User deleted"));
    }

    // ===================== QUIZ MANAGEMENT =====================
    @GetMapping("/quizzes")
    public ResponseEntity<List<Quiz>> getAllQuizzesAdmin() {
        return ResponseEntity.ok(quizRepository.findAll());
    }

    @PostMapping("/quizzes")
    public ResponseEntity<Quiz> createQuiz(@RequestBody Quiz quiz,
            @AuthenticationPrincipal UserDetails userDetails) {
        quiz.setCreatedBy(userDetails.getUsername());
        quiz.setCreatedAt(LocalDateTime.now());
        quiz.setUpdatedAt(LocalDateTime.now());
        quiz.setIsActive(true);
        if (quiz.getQuestions() != null) {
            for (Question q : quiz.getQuestions()) {
                if (q.getId() == null) q.setId(UUID.randomUUID().toString());
            }
        }
        int total = quiz.getQuestions() == null ? 0 :
                quiz.getQuestions().stream().mapToInt(q -> q.getMarks() == null ? 1 : q.getMarks()).sum();
        quiz.setTotalMarks(total);
        return ResponseEntity.ok(quizRepository.save(quiz));
    }

    @PutMapping("/quizzes/{quizId}")
    public ResponseEntity<Quiz> updateQuiz(@PathVariable String quizId, @RequestBody Quiz updated) {
        return quizRepository.findById(quizId).map(q -> {
            q.setTitle(updated.getTitle());
            q.setDescription(updated.getDescription());
            q.setCategory(updated.getCategory());
            q.setDifficulty(updated.getDifficulty());
            q.setTimeLimit(updated.getTimeLimit());
            q.setPassingMarks(updated.getPassingMarks());
            q.setQuestions(updated.getQuestions());
            q.setIsActive(updated.getIsActive());
            q.setUpdatedAt(LocalDateTime.now());
            if (q.getQuestions() != null) {
                int total = q.getQuestions().stream().mapToInt(qu -> qu.getMarks() == null ? 1 : qu.getMarks()).sum();
                q.setTotalMarks(total);
            }
            return ResponseEntity.ok(quizRepository.save(q));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/quizzes/{quizId}")
    public ResponseEntity<Map<String, String>> deleteQuiz(@PathVariable String quizId) {
        quizRepository.deleteById(quizId);
        return ResponseEntity.ok(Map.of("message", "Quiz deleted"));
    }

    @PutMapping("/quizzes/{quizId}/toggle-active")
    public ResponseEntity<Quiz> toggleQuizActive(@PathVariable String quizId) {
        return quizRepository.findById(quizId).map(q -> {
            q.setIsActive(!Boolean.TRUE.equals(q.getIsActive()));
            q.setUpdatedAt(LocalDateTime.now());
            return ResponseEntity.ok(quizRepository.save(q));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ===================== TOPIC MANAGEMENT =====================
    @GetMapping("/topics")
    public ResponseEntity<List<Topic>> getAllTopics() {
        return ResponseEntity.ok(topicRepository.findAll());
    }

    @PostMapping("/topics")
    public ResponseEntity<Topic> createTopic(@RequestBody Topic topic) {
        topic.setCreatedAt(LocalDateTime.now());
        topic.setIsActive(true);
        return ResponseEntity.ok(topicRepository.save(topic));
    }

    @DeleteMapping("/topics/{topicId}")
    public ResponseEntity<Map<String, String>> deleteTopic(@PathVariable String topicId) {
        topicRepository.deleteById(topicId);
        return ResponseEntity.ok(Map.of("message", "Topic deleted"));
    }

    // ===================== ANNOUNCEMENTS / BATCH =====================
    @GetMapping("/users/batch/{batch}")
    public ResponseEntity<List<Map<String, Object>>> getUsersByBatch(@PathVariable String batch) {
        List<Map<String, Object>> users = userRepository.findByBatch(batch).stream().map(u -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", u.getId());
            m.put("name", u.getFirstName() + " " + u.getLastName());
            m.put("email", u.getEmail());
            m.put("department", u.getDepartment());
            m.put("overallProgress", u.getOverallProgress());
            m.put("totalStudyHours", u.getTotalStudyHours());
            m.put("quizzesCompleted", u.getQuizzesCompleted());
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<Map<String, Object>>> getLeaderboard() {
        List<Map<String, Object>> board = userRepository.findByRole("STUDENT").stream()
                .sorted((a, b) -> {
                    int pa = a.getOverallProgress() == null ? 0 : a.getOverallProgress();
                    int pb = b.getOverallProgress() == null ? 0 : b.getOverallProgress();
                    return pb - pa;
                })
                .limit(20)
                .map(u -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", u.getId());
                    m.put("name", u.getFirstName() + " " + u.getLastName());
                    m.put("email", u.getEmail());
                    m.put("batch", u.getBatch());
                    m.put("department", u.getDepartment());
                    m.put("overallProgress", u.getOverallProgress() == null ? 0 : u.getOverallProgress());
                    m.put("totalStudyHours", u.getTotalStudyHours() == null ? 0 : u.getTotalStudyHours());
                    m.put("currentStreak", u.getCurrentStreak() == null ? 0 : u.getCurrentStreak());
                    return m;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(board);
    }
}

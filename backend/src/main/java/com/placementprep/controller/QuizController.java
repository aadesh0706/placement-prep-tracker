package com.placementprep.controller;

import com.placementprep.dto.QuizDTO;
import com.placementprep.dto.QuizAttemptDTO;
import com.placementprep.model.Quiz;
import com.placementprep.model.QuizAttempt;
import com.placementprep.model.UserAnswer;
import com.placementprep.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {
    
    private final QuizService quizService;
    
    @GetMapping
    public ResponseEntity<List<QuizDTO>> getAllQuizzes() {
        return ResponseEntity.ok(quizService.getAllQuizzes());
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<QuizDTO>> getQuizzesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(quizService.getQuizzesByCategory(category));
    }
    
    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<List<QuizDTO>> getQuizzesByDifficulty(@PathVariable String difficulty) {
        return ResponseEntity.ok(quizService.getQuizzesByDifficulty(difficulty));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Quiz> getQuizById(@PathVariable String id) {
        return ResponseEntity.ok(quizService.getQuizById(id));
    }
    
    @PostMapping("/{id}/start")
    public ResponseEntity<QuizAttempt> startQuiz(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(quizService.startQuiz(id, userDetails.getUsername()));
    }
    
    @PostMapping("/attempt/{attemptId}/submit")
    public ResponseEntity<QuizAttempt> submitQuiz(
            @PathVariable String attemptId,
            @RequestBody List<Map<String, Object>> answers) {
        List<UserAnswer> userAnswers = answers.stream().map(a -> {
            String questionId = (String) a.get("questionId");
            Integer selectedOptionIndex = a.get("selectedOptionIndex") instanceof Number
                    ? ((Number) a.get("selectedOptionIndex")).intValue() : null;
            return UserAnswer.builder()
                    .questionId(questionId)
                    .selectedOptionIndex(selectedOptionIndex)
                    .build();
        }).collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(quizService.submitQuiz(attemptId, userAnswers));
    }
    
    @GetMapping("/attempts")
    public ResponseEntity<List<QuizAttemptDTO>> getUserAttempts(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(quizService.getUserAttempts(userDetails.getUsername()));
    }
    
    @GetMapping("/attempts/{attemptId}")
    public ResponseEntity<QuizAttemptDTO> getAttemptById(@PathVariable String attemptId) {
        return ResponseEntity.ok(quizService.getAttemptById(attemptId));
    }
    
    private QuizDTO mapToQuizDTO(com.placementprep.model.Quiz quiz) {
        return QuizDTO.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .category(quiz.getCategory())
                .difficulty(quiz.getDifficulty())
                .timeLimit(quiz.getTimeLimit())
                .totalMarks(quiz.getTotalMarks())
                .passingMarks(quiz.getPassingMarks())
                .isActive(quiz.getIsActive())
                .build();
    }
}

package com.placementprep.service;

import com.placementprep.dto.QuizDTO;
import com.placementprep.dto.QuizAttemptDTO;
import com.placementprep.model.Quiz;
import com.placementprep.model.QuizAttempt;
import com.placementprep.model.User;
import com.placementprep.repository.QuizRepository;
import com.placementprep.repository.QuizAttemptRepository;
import com.placementprep.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {
    
    private final QuizRepository quizRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final UserRepository userRepository;
    
    public List<QuizDTO> getAllQuizzes() {
        return quizRepository.findAll().stream()
                .map(this::mapToQuizDTO)
                .collect(Collectors.toList());
    }
    
    public List<QuizDTO> getQuizzesByCategory(String category) {
        return quizRepository.findByCategory(category).stream()
                .map(this::mapToQuizDTO)
                .collect(Collectors.toList());
    }
    
    public List<QuizDTO> getQuizzesByDifficulty(String difficulty) {
        return quizRepository.findByDifficulty(difficulty).stream()
                .map(this::mapToQuizDTO)
                .collect(Collectors.toList());
    }
    
    public Quiz getQuizById(String id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
    }
    
    public QuizAttempt startQuiz(String quizId, String userId) {
        Quiz quiz = getQuizById(quizId);
        
        QuizAttempt attempt = QuizAttempt.builder()
                .quizId(quizId)
                .userId(userId)
                .answers(new ArrayList<>())
                .totalQuestions(quiz.getQuestions() != null ? quiz.getQuestions().size() : 0)
                .correctAnswers(0)
                .marksObtained(0)
                .totalMarks(quiz.getTotalMarks())
                .status("IN_PROGRESS")
                .startedAt(LocalDateTime.now())
                .build();
        
        return quizAttemptRepository.save(attempt);
    }
    
    public QuizAttempt submitQuiz(String attemptId, List<UserAnswer> answers) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Quiz attempt not found"));
        
        Quiz quiz = getQuizById(attempt.getQuizId());
        
        // Evaluate answers
        int correctAnswers = 0;
        int marksObtained = 0;
        Map<String, Integer> categoryWiseScore = new HashMap<>();
        List<String> weakAreas = new ArrayList<>();
        List<String> strongAreas = new ArrayList<>();
        
        for (UserAnswer answer : answers) {
            // Find the question in quiz
            if (quiz.getQuestions() != null) {
                Optional<Question> questionOpt = quiz.getQuestions().stream()
                        .filter(q -> q.getId().equals(answer.getQuestionId()))
                        .findFirst();
                
                if (questionOpt.isPresent()) {
                    Question question = questionOpt.get();
                    boolean isCorrect = question.getCorrectOptionIndex().equals(answer.getSelectedOptionIndex());
                    answer.setIsCorrect(isCorrect);
                    
                    if (isCorrect) {
                        correctAnswers++;
                        marksObtained += question.getMarks();
                        
                        // Track category
                        String category = question.getCategory();
                        categoryWiseScore.put(category, categoryWiseScore.getOrDefault(category, 0) + question.getMarks());
                    }
                    
                    answer.setMarksObtained(isCorrect ? question.getMarks() : 0);
                }
            }
        }
        
        // Calculate percentage and grade
        double percentage = attempt.getTotalMarks() > 0 ? 
                (marksObtained * 100.0 / attempt.getTotalMarks()) : 0;
        
        String grade;
        if (percentage >= 90) grade = "A";
        else if (percentage >= 80) grade = "B";
        else if (percentage >= 70) grade = "C";
        else if (percentage >= 60) grade = "D";
        else grade = "F";
        
        // Determine weak and strong areas
        for (Map.Entry<String, Integer> entry : categoryWiseScore.entrySet()) {
            // This is simplified - in production, compare with thresholds
            weakAreas.add(entry.getKey());
        }
        
        attempt.setAnswers(answers);
        attempt.setCorrectAnswers(correctAnswers);
        attempt.setMarksObtained(marksObtained);
        attempt.setPercentage(percentage);
        attempt.setGrade(grade);
        attempt.setCategoryWiseScore(categoryWiseScore);
        attempt.setWeakAreas(weakAreas);
        attempt.setStrongAreas(strongAreas);
        attempt.setStatus("COMPLETED");
        attempt.setSubmittedAt(LocalDateTime.now());
        
        // Update user stats
        updateUserStats(attempt.getUserId());
        
        return quizAttemptRepository.save(attempt);
    }
    
    public List<QuizAttemptDTO> getUserAttempts(String userId) {
        return quizAttemptRepository.findByUserIdOrderBySubmittedAtDesc(userId).stream()
                .map(this::mapToQuizAttemptDTO)
                .collect(Collectors.toList());
    }
    
    public QuizAttemptDTO getAttemptById(String attemptId) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));
        return mapToQuizAttemptDTO(attempt);
    }
    
    private void updateUserStats(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<QuizAttempt> attempts = quizAttemptRepository.findByUserId(userId);
        
        user.setQuizzesCompleted(attempts.size());
        user.setOverallProgress(calculateOverallProgress(user));
        userRepository.save(user);
    }
    
    private int calculateOverallProgress(User user) {
        // Simplified progress calculation
        return Math.min(100, user.getQuizzesCompleted() * 5 + user.getTotalStudyHours());
    }
    
    private QuizDTO mapToQuizDTO(Quiz quiz) {
        return QuizDTO.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .category(quiz.getCategory())
                .difficulty(quiz.getDifficulty())
                .timeLimit(quiz.getTimeLimit())
                .totalMarks(quiz.getTotalMarks())
                .passingMarks(quiz.getPassingMarks())
                .questionCount(quiz.getQuestions() != null ? quiz.getQuestions().size() : 0)
                .isActive(quiz.getIsActive())
                .build();
    }
    
    private QuizAttemptDTO mapToQuizAttemptDTO(QuizAttempt attempt) {
        Quiz quiz = getQuizById(attempt.getQuizId());
        
        return QuizAttemptDTO.builder()
                .id(attempt.getId())
                .quizId(attempt.getQuizId())
                .quizTitle(quiz != null ? quiz.getTitle() : "Unknown")
                .totalQuestions(attempt.getTotalQuestions())
                .correctAnswers(attempt.getCorrectAnswers())
                .marksObtained(attempt.getMarksObtained())
                .totalMarks(attempt.getTotalMarks())
                .percentage(attempt.getPercentage())
                .grade(attempt.getGrade())
                .categoryWiseScore(attempt.getCategoryWiseScore())
                .weakAreas(attempt.getWeakAreas())
                .strongAreas(attempt.getStrongAreas())
                .submittedAt(attempt.getSubmittedAt())
                .build();
    }
}

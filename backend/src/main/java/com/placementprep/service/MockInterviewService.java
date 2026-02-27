package com.placementprep.service;

import com.placementprep.model.MockInterview;
import com.placementprep.model.InterviewQuestion;
import com.placementprep.repository.MockInterviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MockInterviewService {
    
    private final MockInterviewRepository interviewRepository;
    
    public MockInterview createInterview(String userId, MockInterview request) {
        // Generate AI questions based on type and topics
        List<InterviewQuestion> questions = generateQuestions(request.getType(), request.getTopics());
        
        MockInterview interview = MockInterview.builder()
                .userId(userId)
                .type(request.getType())
                .difficulty(request.getDifficulty())
                .topics(request.getTopics())
                .targetCompany(request.getTargetCompany())
                .questions(questions)
                .responses(new ArrayList<>())
                .status("NOT_STARTED")
                .duration(30) // default 30 minutes
                .createdAt(LocalDateTime.now())
                .build();
        
        return interviewRepository.save(interview);
    }
    
    public MockInterview startInterview(String interviewId) {
        MockInterview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview not found"));
        
        interview.setStatus("IN_PROGRESS");
        interview.setStartedAt(LocalDateTime.now());
        
        return interviewRepository.save(interview);
    }
    
    public MockInterview submitResponse(String interviewId, String questionId, String response) {
        MockInterview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview not found"));
        
        // Add response
        // Simplified - in production, integrate with AI for evaluation
        interview.getResponses().add(createResponse(questionId, response));
        
        return interviewRepository.save(interview);
    }
    
    public MockInterview completeInterview(String interviewId, Map<String, Object> evaluation) {
        MockInterview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview not found"));
        
        interview.setStatus("COMPLETED");
        interview.setCompletedAt(LocalDateTime.now());
        
        // Set evaluation scores
        if (evaluation.containsKey("communication")) {
            interview.getEvaluationScores().put("communication", (Integer) evaluation.get("communication"));
        }
        if (evaluation.containsKey("technical")) {
            interview.getEvaluationScores().put("technical", (Integer) evaluation.get("technical"));
        }
        if (evaluation.containsKey("confidence")) {
            interview.getEvaluationScores().put("confidence", (Integer) evaluation.get("confidence"));
        }
        
        // Calculate overall score
        int total = interview.getEvaluationScores().values().stream()
                .mapToInt(Integer::intValue).sum();
        interview.setOverallScore(total / 3);
        
        interview.setOverallFeedback((String) evaluation.get("feedback"));
        
        return interviewRepository.save(interview);
    }
    
    public List<MockInterview> getUserInterviews(String userId) {
        return interviewRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    public MockInterview getInterviewById(String id) {
        return interviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Interview not found"));
    }
    
    private List<InterviewQuestion> generateQuestions(String type, List<String> topics) {
        List<InterviewQuestion> questions = new ArrayList<>();
        
        // Technical questions
        if ("TECHNICAL".equals(type) || "FINAL".equals(type)) {
            questions.addAll(generateTechnicalQuestions(topics));
        }
        
        // HR questions
        if ("HR".equals(type) || "MR".equals(type) || "FINAL".equals(type)) {
            questions.addAll(generateHRQuestions());
        }
        
        return questions;
    }
    
    private List<InterviewQuestion> generateTechnicalQuestions(List<String> topics) {
        // Sample technical questions - in production, integrate with AI
        List<InterviewQuestion> questions = new ArrayList<>();
        
        questions.add(InterviewQuestion.builder()
                .id(UUID.randomUUID().toString())
                .question("Explain the difference between ArrayList and LinkedList.")
                .category("DSA")
                .type("TECHNICAL")
                .expectedDuration(120)
                .sampleAnswer("ArrayList uses dynamic array... LinkedList uses doubly linked list...")
                .build());
        
        questions.add(InterviewQuestion.builder()
                .id(UUID.randomUUID().toString())
                .question("What is the time complexity of HashMap get() operation?")
                .category("DSA")
                .type("TECHNICAL")
                .expectedDuration(60)
                .sampleAnswer("Average case O(1), worst case O(n)...")
                .build());
        
        questions.add(InterviewQuestion.builder()
                .id(UUID.randomUUID().toString())
                .question("Explain ACID properties of databases.")
                .category("DBMS")
                .type("TECHNICAL")
                .expectedDuration(180)
                .sampleAnswer("ACID stands for Atomicity, Consistency, Isolation, Durability...")
                .build());
        
        return questions;
    }
    
    private List<InterviewQuestion> generateHRQuestions() {
        List<InterviewQuestion> questions = new ArrayList<>();
        
        questions.add(InterviewQuestion.builder()
                .id(UUID.randomUUID().toString())
                .question("Tell me about yourself.")
                .category("HR")
                .type("BEHAVIORAL")
                .expectedDuration(120)
                .sampleAnswer("I am a final year student...")
                .build());
        
        questions.add(InterviewQuestion.builder()
                .id(UUID.randomUUID().toString())
                .question("What are your strengths and weaknesses?")
                .category("HR")
                .type("BEHAVIORAL")
                .expectedDuration(120)
                .sampleAnswer("My strength is problem solving...")
                .build());
        
        questions.add(InterviewQuestion.builder()
                .id(UUID.randomUUID().toString())
                .question("Why do you want to join our company?")
                .category("HR")
                .type("SITUATIONAL")
                .expectedDuration(120)
                .sampleAnswer("I admire your company's innovation...")
                .build());
        
        return questions;
    }
    
    private com.placementprep.model.UserResponse createResponse(String questionId, String text) {
        return com.placementprep.model.UserResponse.builder()
                .questionId(questionId)
                .textResponse(text)
                .duration(0)
                .evaluation(new HashMap<>())
                .build();
    }
}

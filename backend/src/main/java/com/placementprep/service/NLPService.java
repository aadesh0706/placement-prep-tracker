package com.placementprep.service;

import com.placementprep.model.HRAnswer;
import com.placementprep.model.Resume;
import com.placementprep.repository.HRAnswerRepository;
import com.placementprep.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class NLPService {
    
    private final ResumeRepository resumeRepository;
    private final HRAnswerRepository hrAnswerRepository;
    
    // Sample keywords for resume analysis
    private static final Map<String, List<String>> SKILL_KEYWORDS = Map.of(
            "Programming Languages", Arrays.asList("java", "python", "javascript", "c++", "c#", "ruby", "go", "rust"),
            "Web Technologies", Arrays.asList("html", "css", "react", "angular", "vue", "nodejs", "express", "django", "spring"),
            "Databases", Arrays.asList("sql", "mysql", "postgresql", "mongodb", "redis", "oracle", "firebase"),
            "Tools", Arrays.asList("git", "docker", "kubernetes", "jenkins", "aws", "azure", "jira"),
            "Soft Skills", Arrays.asList("leadership", "teamwork", "communication", "problem-solving", "analytical")
    );
    
    public Resume analyzeResume(String userId, String fileName, String extractedText) {
        Resume resume = Resume.builder()
                .userId(userId)
                .fileName(fileName)
                .extractedText(extractedText)
                .parsedData(extractResumeData(extractedText))
                .status("EVALUATING")
                .uploadedAt(LocalDateTime.now())
                .build();
        
        Resume savedResume = resumeRepository.save(resume);
        
        // Perform AI evaluation
        Resume evaluated = evaluateResume(savedResume);
        
        return resumeRepository.save(evaluated);
    }
    
    public HRAnswer analyzeHRAnswer(String userId, String question, String userAnswer) {
        HRAnswer hrAnswer = HRAnswer.builder()
                .userId(userId)
                .question(question)
                .userAnswer(userAnswer)
                .status("PENDING")
                .submittedAt(LocalDateTime.now())
                .build();
        
        HRAnswer saved = hrAnswerRepository.save(hrAnswer);
        
        // Evaluate with AI
        HRAnswer evaluated = evaluateHRAnswer(saved);
        
        return hrAnswerRepository.save(evaluated);
    }
    
    public List<Resume> getUserResumes(String userId) {
        return resumeRepository.findByUserId(userId);
    }
    
    public List<HRAnswer> getUserHRAnswers(String userId) {
        return hrAnswerRepository.findByUserIdOrderBySubmittedAtDesc(userId);
    }
    
    private Map<String, Object> extractResumeData(String text) {
        Map<String, Object> data = new HashMap<>();
        
        String lowerText = text.toLowerCase();
        
        // Extract skills
        List<String> foundSkills = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : SKILL_KEYWORDS.entrySet()) {
            for (String skill : entry.getValue()) {
                if (lowerText.contains(skill)) {
                    foundSkills.add(skill);
                }
            }
        }
        data.put("skills", foundSkills);
        
        // Extract email
        if (lowerText.contains("@")) {
            int atIndex = lowerText.indexOf("@");
            String email = extractEmail(text, atIndex);
            data.put("email", email);
        }
        
        // Extract phone (simple pattern)
        if (lowerText.contains("+91") || lowerText.contains("91") || lowerText.matches(".*\\d{10}.*")) {
            data.put("phone", "Found");
        }
        
        // Estimate experience
        int experienceYears = estimateExperience(lowerText);
        data.put("experience", experienceYears);
        
        return data;
    }
    
    private String extractEmail(String text, int atIndex) {
        int start = atIndex - 30;
        int end = atIndex + 30;
        if (start < 0) start = 0;
        if (end > text.length()) end = text.length();
        String snippet = text.substring(start, end);
        int spaceBefore = snippet.lastIndexOf(' ');
        int spaceAfter = snippet.indexOf(' ', atIndex - start);
        if (spaceBefore >= 0 && spaceAfter >= 0) {
            return snippet.substring(spaceBefore + 1, spaceAfter);
        }
        return "";
    }
    
    private int estimateExperience(String text) {
        // Simple heuristic based on mentions
        int score = 0;
        if (text.contains("fresher") || text.contains("entry-level")) score = 0;
        else if (text.contains("1 year") || text.contains("one year")) score = 1;
        else if (text.contains("2 years") || text.contains("two years")) score = 2;
        else if (text.contains("3 years")) score = 3;
        else if (text.contains("4 years")) score = 4;
        else if (text.contains("5+ years") || text.contains("5 years")) score = 5;
        
        return score;
    }
    
    private Resume evaluateResume(Resume resume) {
        String text = resume.getExtractedText().toLowerCase();
        Map<String, Object> parsed = resume.getParsedData();
        
        // Calculate scores
        int clarityScore = calculateClarityScore(text);
        int relevanceScore = calculateRelevanceScore(text, parsed);
        int impactScore = calculateImpactScore(text);
        int formattingScore = calculateFormattingScore(text);
        
        int overallScore = (clarityScore + relevanceScore + impactScore + formattingScore) / 4;
        
        resume.setOverallScore(overallScore);
        
        // Generate suggestions
        List<String> suggestions = generateResumeSuggestions(clarityScore, relevanceScore, impactScore, formattingScore);
        resume.setSuggestions(suggestions);
        
        // Generate strengths and weaknesses
        resume.setStrengths(generateStrengths(clarityScore, relevanceScore, impactScore, formattingScore));
        resume.setWeaknesses(generateWeaknesses(clarityScore, relevanceScore, impactScore, formattingScore));
        
        // Simple ATS scoring
        double atsScore = calculateATSScore(text, parsed);
        resume.setAtsScore(atsScore);
        
        resume.setStatus("EVALUATED");
        resume.setAnalyzedAt(LocalDateTime.now());
        
        return resume;
    }
    
    private int calculateClarityScore(String text) {
        // Based on length and structure
        int score = 70;
        if (text.length() > 500 && text.length() < 2000) score += 20;
        if (text.contains("objective") || text.contains("summary")) score += 10;
        return Math.min(100, score);
    }
    
    private int calculateRelevanceScore(String text, Map<String, Object> parsed) {
        int score = 50;
        List<String> skills = (List<String>) parsed.get("skills");
        if (skills != null && skills.size() > 5) score += 30;
        if (skills != null && skills.size() > 10) score += 20;
        return Math.min(100, score);
    }
    
    private int calculateImpactScore(String text) {
        int score = 60;
        // Check for action verbs and quantifiable achievements
        String[] actionVerbs = {"achieved", "led", "developed", "implemented", "managed", "created"};
        for (String verb : actionVerbs) {
            if (text.contains(verb)) score += 5;
        }
        return Math.min(100, score);
    }
    
    private int calculateFormattingScore(String text) {
        // Simple check - proper spacing and structure
        return 75; // Simplified
    }
    
    private double calculateATSScore(String text, Map<String, Object> parsed) {
        double score = 50.0;
        List<String> skills = (List<String>) parsed.get("skills");
        if (skills != null) {
            score += skills.size() * 2;
        }
        if (text.contains("@")) score += 10;
        return Math.min(100.0, score);
    }
    
    private List<String> generateResumeSuggestions(int clarity, int relevance, int impact, int formatting) {
        List<String> suggestions = new ArrayList<>();
        if (clarity < 80) suggestions.add("Improve the clarity of your resume by adding a professional summary");
        if (relevance < 80) suggestions.add("Add more relevant skills for your target job profile");
        if (impact < 80) suggestions.add("Use action verbs and quantify your achievements");
        if (formatting < 80) suggestions.add("Improve formatting with consistent fonts and proper spacing");
        return suggestions;
    }
    
    private List<String> generateStrengths(int clarity, int relevance, int impact, int formatting) {
        List<String> strengths = new ArrayList<>();
        if (clarity >= 80) strengths.add("Well-structured and clear resume");
        if (relevance >= 80) strengths.add("Strong relevant skills section");
        if (impact >= 80) strengths.add("Good use of action verbs");
        if (formatting >= 80) strengths.add("Professional formatting");
        return strengths;
    }
    
    private List<String> generateWeaknesses(int clarity, int relevance, int impact, int formatting) {
        List<String> weaknesses = new ArrayList<>();
        if (clarity < 70) weaknesses.add("Resume lacks clarity");
        if (relevance < 70) weaknesses.add("Need more relevant skills");
        if (impact < 70) weaknesses.add("Quantify your achievements");
        return weaknesses;
    }
    
    private HRAnswer evaluateHRAnswer(HRAnswer answer) {
        String response = answer.getUserAnswer().toLowerCase();
        
        // Simple scoring based on length and keywords
        int clarityScore = calculateHRClarityScore(response);
        int confidenceScore = calculateHRConfidenceScore(response);
        int relevanceScore = calculateHRRelevanceScore(response);
        int articulationScore = calculateHRArticulationScore(response);
        
        int overallScore = (clarityScore + confidenceScore + relevanceScore + articulationScore) / 4;
        
        answer.setScore(overallScore);
        answer.setFeedback(generateHRFeedback(overallScore));
        answer.setSuggestions(generateHRSuggestions(overallScore));
        answer.setStatus("EVALUATED");
        answer.setEvaluatedAt(LocalDateTime.now());
        
        return answer;
    }
    
    private int calculateHRClarityScore(String response) {
        if (response.length() < 50) return 40;
        if (response.length() < 150) return 70;
        if (response.length() < 300) return 85;
        return 90;
    }
    
    private int calculateHRConfidenceScore(String response) {
        int score = 60;
        String[] confidentPhrases = {"confident", "believe", "certain", "sure", "experienced", "skilled"};
        for (String phrase : confidentPhrases) {
            if (response.contains(phrase)) score += 8;
        }
        return Math.min(100, score);
    }
    
    private int calculateHRRelevanceScore(String response) {
        // Check if answer addresses the question
        return 75; // Simplified
    }
    
    private int calculateHRArticulationScore(String response) {
        // Check for proper sentence structure
        if (response.contains(".") && response.contains(",")) return 80;
        return 60;
    }
    
    private String generateHRFeedback(int score) {
        if (score >= 80) return "Excellent response! Well-articulated and confident.";
        if (score >= 60) return "Good response. Could be improved with more specific examples.";
        if (score >= 40) return "Fair response. Try to be more specific and confident.";
        return "Needs improvement. Practice more and be specific in your answers.";
    }
    
    private List<String> generateHRSuggestions(int score) {
        List<String> suggestions = new ArrayList<>();
        if (score < 80) suggestions.add("Be more specific with examples");
        if (score < 70) suggestions.add("Show more confidence in your responses");
        if (score < 60) suggestions.add("Keep your answers concise but informative");
        return suggestions;
    }
}

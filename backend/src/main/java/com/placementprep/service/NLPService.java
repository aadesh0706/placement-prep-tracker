package com.placementprep.service;

import com.placementprep.model.HRAnswer;
import com.placementprep.model.QuizAttempt;
import com.placementprep.model.Resume;
import com.placementprep.repository.HRAnswerRepository;
import com.placementprep.repository.QuizAttemptRepository;
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
    private final QuizAttemptRepository quizAttemptRepository;
    
    // Sample keywords for resume analysis
    private static final Map<String, List<String>> SKILL_KEYWORDS = Map.of(
            "Programming Languages", Arrays.asList("java", "python", "javascript", "c++", "c#", "ruby", "go", "rust"),
            "Web Technologies", Arrays.asList("html", "css", "react", "angular", "vue", "nodejs", "express", "django", "spring"),
            "Databases", Arrays.asList("sql", "mysql", "postgresql", "mongodb", "redis", "oracle", "firebase"),
            "Tools", Arrays.asList("git", "docker", "kubernetes", "jenkins", "aws", "azure", "jira"),
            "Soft Skills", Arrays.asList("leadership", "teamwork", "communication", "problem-solving", "analytical")
    );
    
    public Resume analyzeResume(String userId, String fileName, String extractedText) {
        Map<String, Object> parsedData = extractResumeData(extractedText);
        
        Resume resume = Resume.builder()
                .userId(userId)
                .fileName(fileName)
                .extractedText(extractedText)
                .parsedData(parsedData)
                .extractedSkills((List<String>) parsedData.getOrDefault("skills", new ArrayList<>()))
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
    
    public List<QuizAttempt> getUserQuizAttempts(String userId) {
        return quizAttemptRepository.findByUserId(userId);
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
    
    // ============ ENHANCED AI/ML FEATURES ============
    
    /**
     * Analyze resume against a specific job description
     */
    public Map<String, Object> matchResumeToJob(String resumeText, String jobDescription) {
        Map<String, Object> result = new HashMap<>();
        
        String resumeLower = resumeText.toLowerCase();
        String jobLower = jobDescription.toLowerCase();
        
        // Extract required skills from job description
        List<String> requiredSkills = extractRequiredSkills(jobLower);
        
        // Check which skills match
        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();
        
        for (String skill : requiredSkills) {
            if (resumeLower.contains(skill)) {
                matchedSkills.add(skill);
            } else {
                missingSkills.add(skill);
            }
        }
        
        double matchPercentage = requiredSkills.isEmpty() ? 0 : 
            (matchedSkills.size() * 100.0) / requiredSkills.size();
        
        result.put("matchPercentage", matchPercentage);
        result.put("matchedSkills", matchedSkills);
        result.put("missingSkills", missingSkills);
        result.put("recommendations", generateJobMatchRecommendations(matchedSkills, missingSkills));
        
        return result;
    }
    
    private List<String> extractRequiredSkills(String jobText) {
        List<String> skills = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : SKILL_KEYWORDS.entrySet()) {
            for (String skill : entry.getValue()) {
                if (jobText.contains(skill)) {
                    skills.add(skill);
                }
            }
        }
        return skills;
    }
    
    private List<String> generateJobMatchRecommendations(List<String> matched, List<String> missing) {
        List<String> recommendations = new ArrayList<>();
        if (!missing.isEmpty()) {
            recommendations.add("Learn these missing skills: " + String.join(", ", missing));
        }
        if (matched.size() < 5) {
            recommendations.add("Add more relevant technical skills to your resume");
        }
        recommendations.add("Tailor your resume keywords to match the job description");
        return recommendations;
    }
    
    /**
     * Generate company-specific interview preparation tips
     */
    public Map<String, Object> getCompanyPrepTips(String company, String role) {
        Map<String, Object> tips = new HashMap<>();
        
        String companyLower = company.toLowerCase();
        
        List<String> focusAreas = new ArrayList<>();
        List<String> commonQuestions = new ArrayList<>();
        List<String> preparationResources = new ArrayList<>();
        
        // Company-specific tips
        if (companyLower.contains("google") || companyLower.contains("meta") || companyLower.contains("amazon")) {
            focusAreas.add("Data Structures & Algorithms");
            focusAreas.add("System Design");
            focusAreas.add("Problem Solving");
            commonQuestions.add("Design a URL shortener");
            commonQuestions.add("Find the median of two sorted arrays");
            commonQuestions.add("Explain OOP concepts with real examples");
        }
        
        if (companyLower.contains("amazon")) {
            focusAreas.add("Leadership Principles");
            commonQuestions.add("Tell me about a time you faced a challenging situation");
            commonQuestions.add("Describe a time you showed customer obsession");
            commonQuestions.add("Give an example of a goal you reached and how you achieved it");
        }
        
        if (companyLower.contains("microsoft")) {
            focusAreas.add("Problem Solving");
            focusAreas.add("C#/.NET fundamentals");
            focusAreas.add("Cloud Computing (Azure)");
            commonQuestions.add("Design a parking lot system");
            commonQuestions.add("Explain the difference between abstract class and interface");
        }
        
        if (companyLower.contains("startup") || companyLower.contains("flipkart")) {
            focusAreas.add("Full Stack Development");
            focusAreas.add("Scalability concepts");
            focusAreas.add("Fast-paced learning ability");
            commonQuestions.add("Why do you want to join a startup?");
            commonQuestions.add("Tell us about a project you built from scratch");
        }
        
        // Default tips
        if (focusAreas.isEmpty()) {
            focusAreas.add("Core technical subjects");
            focusAreas.add("Problem solving skills");
            commonQuestions.add("Tell me about yourself");
            commonQuestions.add("Why do you want to join this company?");
        }
        
        preparationResources.add("GeeksforGeeks - Practice problems");
        preparationResources.add("LeetCode - Interview preparation");
        preparationResources.add("HackerRank - Skill assessment");
        
        tips.put("focusAreas", focusAreas);
        tips.put("commonQuestions", commonQuestions);
        tips.put("preparationResources", preparationResources);
        tips.put("estimatedPrepTime", calculatePrepTime(focusAreas.size()));
        
        return tips;
    }
    
    private String calculatePrepTime(int focusAreas) {
        int hours = focusAreas * 10;
        return hours + " hours";
    }
    
    /**
     * AI-powered weak area analysis based on quiz performance
     */
    public Map<String, Object> analyzeWeakAreas(List<QuizAttempt> attempts) {
        Map<String, Object> analysis = new HashMap<>();
        
        if (attempts == null || attempts.isEmpty()) {
            analysis.put("message", "No quiz data available for analysis");
            return analysis;
        }
        
        // Group scores by category
        Map<String, List<Integer>> categoryScores = new HashMap<>();
        for (QuizAttempt attempt : attempts) {
            if (attempt.getCategoryWiseScore() != null) {
                attempt.getCategoryWiseScore().forEach((category, score) -> 
                    categoryScores.computeIfAbsent(category, k -> new ArrayList<>()).add(score));
            }
        }
        
        // Identify weak and strong areas
        List<String> weakAreas = new ArrayList<>();
        List<String> strongAreas = new ArrayList<>();
        Map<String, Double> categoryAverages = new HashMap<>();
        
        for (Map.Entry<String, List<Integer>> entry : categoryScores.entrySet()) {
            double avg = entry.getValue().stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0);
            categoryAverages.put(entry.getKey(), avg);
            
            if (avg < 60) {
                weakAreas.add(entry.getKey() + " (" + String.format("%.0f", avg) + "%)");
            } else if (avg >= 75) {
                strongAreas.add(entry.getKey() + " (" + String.format("%.0f", avg) + "%)");
            }
        }
        
        // Generate study recommendations
        List<String> recommendations = new ArrayList<>();
        for (String weak : weakAreas) {
            String topic = weak.split(" ")[0];
            recommendations.add("Focus more on " + topic + " - practice at least 5 problems daily");
            recommendations.add("Watch video tutorials on " + topic + " concepts");
            recommendations.add("Take notes on " + topic + " fundamentals");
        }
        
        analysis.put("weakAreas", weakAreas);
        analysis.put("strongAreas", strongAreas);
        analysis.put("categoryAverages", categoryAverages);
        analysis.put("recommendations", recommendations);
        analysis.put("priorityOrder", weakAreas);
        
        return analysis;
    }
    
    /**
     * Generate personalized study plan based on goals and current level
     */
    public Map<String, Object> generateStudyPlan(String goal, String currentLevel, int hoursPerWeek) {
        Map<String, Object> plan = new HashMap<>();
        
        List<Map<String, String>> schedule = new ArrayList<>();
        
        // Determine focus areas based on goal
        List<String> focusAreas = getFocusAreasForGoal(goal);
        int weeksToPrep = calculateWeeksToPrep(currentLevel, hoursPerWeek);
        
        for (int week = 1; week <= weeksToPrep; week++) {
            Map<String, String> weekPlan = new HashMap<>();
            weekPlan.put("week", "Week " + week);
            weekPlan.put("focus", focusAreas.get((week - 1) % focusAreas.size()));
            weekPlan.put("tasks", generateWeeklyTasks(focusAreas.get((week - 1) % focusAreas.size()), hoursPerWeek));
            weekPlan.put("milestone", generateMilestone(week, weeksToPrep));
            schedule.add(weekPlan);
        }
        
        plan.put("schedule", schedule);
        plan.put("totalWeeks", weeksToPrep);
        plan.put("focusAreas", focusAreas);
        plan.put("estimatedHours", weeksToPrep * hoursPerWeek);
        
        return plan;
    }
    
    private List<String> getFocusAreasForGoal(String goal) {
        String goalLower = goal.toLowerCase();
        List<String> areas = new ArrayList<>();
        
        if (goalLower.contains("software") || goalLower.contains("developer") || goalLower.contains("engineer")) {
            areas.add("DSA Fundamentals");
            areas.add("Arrays & Strings");
            areas.add("Linked Lists & Trees");
            areas.add("Dynamic Programming");
            areas.add("System Design Basics");
            areas.add("OS & Networks");
        } else if (goalLower.contains("data")) {
            areas.add("SQL & Databases");
            areas.add("Statistics & Probability");
            areas.add("Python for Data Science");
            areas.add("Machine Learning Basics");
            areas.add("Data Visualization");
        } else if (goalLower.contains("manager") || goalLower.contains("lead")) {
            areas.add("Leadership Principles");
            areas.add("Project Management");
            areas.add("Communication Skills");
            areas.add("Decision Making");
        } else {
            areas.add("Technical Fundamentals");
            areas.add("Problem Solving");
            areas.add("Communication");
            areas.add("Domain Knowledge");
        }
        
        return areas;
    }
    
    private int calculateWeeksToPrep(String level, int hoursPerWeek) {
        switch (level.toUpperCase()) {
            case "BEGINNER": return 16;
            case "INTERMEDIATE": return 12;
            case "ADVANCED": return 8;
            default: return 12;
        }
    }
    
    private String generateWeeklyTasks(String focus, int hoursPerWeek) {
        int problemsPerDay = Math.max(2, hoursPerWeek / 10);
        return String.format("Solve %d %s problems daily, review concepts for %d hours/week", 
                problemsPerDay, focus, hoursPerWeek / 3);
    }
    
    private String generateMilestone(int week, int total) {
        if (week == 1) return "Complete basics";
        if (week == total / 2) return "Halfway through syllabus";
        if (week == total - 2) return "Start mock interviews";
        if (week == total) return "Placement ready!";
        return "Continue practicing";
    }
}

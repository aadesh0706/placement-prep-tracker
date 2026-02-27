package com.placementprep.repository;

import com.placementprep.model.Quiz;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuizRepository extends MongoRepository<Quiz, String> {
    List<Quiz> findByCategory(String category);
    List<Quiz> findByDifficulty(String difficulty);
    List<Quiz> findByCreatedBy(String createdBy);
    List<Quiz> findByBatch(String batch);
    List<Quiz> findByCategoryAndDifficulty(String category, String difficulty);
    List<Quiz> findByIsActive(Boolean isActive);
}

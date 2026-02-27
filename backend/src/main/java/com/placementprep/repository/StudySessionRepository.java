package com.placementprep.repository;

import com.placementprep.model.StudySession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StudySessionRepository extends MongoRepository<StudySession, String> {
    List<StudySession> findByUserId(String userId);
    List<StudySession> findByUserIdOrderByCreatedAtDesc(String userId);
    List<StudySession> findByUserIdAndCategory(String userId, String category);
    List<StudySession> findByUserIdBetweenCreatedAt(String userId, LocalDateTime start, LocalDateTime end);
}

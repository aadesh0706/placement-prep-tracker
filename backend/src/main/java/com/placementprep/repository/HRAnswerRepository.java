package com.placementprep.repository;

import com.placementprep.model.HRAnswer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HRAnswerRepository extends MongoRepository<HRAnswer, String> {
    List<HRAnswer> findByUserId(String userId);
    List<HRAnswer> findByUserIdOrderBySubmittedAtDesc(String userId);
    List<HRAnswer> findByStatus(String status);
}

package com.placementprep.repository;

import com.placementprep.model.MockInterview;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MockInterviewRepository extends MongoRepository<MockInterview, String> {
    List<MockInterview> findByUserId(String userId);
    List<MockInterview> findByUserIdOrderByCreatedAtDesc(String userId);
    List<MockInterview> findByUserIdAndType(String userId, String type);
    List<MockInterview> findByUserIdAndStatus(String userId, String status);
}

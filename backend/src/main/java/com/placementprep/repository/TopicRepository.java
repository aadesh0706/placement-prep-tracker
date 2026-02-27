package com.placementprep.repository;

import com.placementprep.model.Topic;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TopicRepository extends MongoRepository<Topic, String> {
    List<Topic> findByCategory(String category);
    List<Topic> findByIsActive(Boolean isActive);
    List<Topic> findByCategoryAndIsActive(String category, Boolean isActive);
}

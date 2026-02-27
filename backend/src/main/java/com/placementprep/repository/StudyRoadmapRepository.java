package com.placementprep.repository;

import com.placementprep.model.StudyRoadmap;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudyRoadmapRepository extends MongoRepository<StudyRoadmap, String> {
    Optional<StudyRoadmap> findByUserId(String userId);
    List<StudyRoadmap> findByStatus(String status);
    Boolean existsByUserId(String userId);
}

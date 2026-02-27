package com.placementprep.repository;

import com.placementprep.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    List<User> findByRole(String role);
    List<User> findByBatch(String batch);
    List<User> findByDepartment(String department);
    List<User> findByBatchAndDepartment(String batch, String department);
    List<User> findByIsActive(Boolean isActive);
}

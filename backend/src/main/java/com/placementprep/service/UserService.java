package com.placementprep.service;

import com.placementprep.dto.UserDTO;
import com.placementprep.model.User;
import com.placementprep.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    
    public UserDTO getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToUserDTO(user);
    }
    
    public UserDTO updateProfile(String email, Map<String, Object> updates) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (updates.containsKey("firstName")) {
            user.setFirstName((String) updates.get("firstName"));
        }
        if (updates.containsKey("lastName")) {
            user.setLastName((String) updates.get("lastName"));
        }
        if (updates.containsKey("phone")) {
            user.setPhone((String) updates.get("phone"));
        }
        if (updates.containsKey("batch")) {
            user.setBatch((String) updates.get("batch"));
        }
        if (updates.containsKey("department")) {
            user.setDepartment((String) updates.get("department"));
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        return mapToUserDTO(user);
    }
    
    private UserDTO mapToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .batch(user.getBatch())
                .department(user.getDepartment())
                .enrollmentNumber(user.getEnrollmentNumber())
                .phone(user.getPhone())
                .topicProgress(user.getTopicProgress())
                .overallProgress(user.getOverallProgress())
                .totalStudyHours(user.getTotalStudyHours())
                .currentStreak(user.getCurrentStreak())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

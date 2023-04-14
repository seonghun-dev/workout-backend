package com.tomtom.scoop.domain.user.repository;

import com.tomtom.scoop.domain.user.model.entity.User;
import com.tomtom.scoop.domain.user.model.entity.UserExerciseLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserExerciseLevelRepository extends JpaRepository<UserExerciseLevel, Long> {
    List<UserExerciseLevel> findAllByUser(User user);
}

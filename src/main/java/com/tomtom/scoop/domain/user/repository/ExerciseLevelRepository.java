package com.tomtom.scoop.domain.user.repository;

import com.tomtom.scoop.domain.user.model.entity.ExerciseLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExerciseLevelRepository extends JpaRepository<ExerciseLevel, Long> {
    Optional<ExerciseLevel> findByLevelAndExerciseId(String exerciseLevel, Long id);
}
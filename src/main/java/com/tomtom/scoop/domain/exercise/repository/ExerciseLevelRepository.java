package com.tomtom.scoop.domain.exercise.repository;

import com.tomtom.scoop.domain.exercise.model.entity.ExerciseLevel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseLevelRepository extends JpaRepository<ExerciseLevel, Long> {
}

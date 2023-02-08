package com.tomtom.scoop.domain.user.repository;

import com.tomtom.scoop.domain.user.model.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    Optional<Exercise> findByName(String exerciseName);
}
package com.tomtom.scoop.domain.user.model.dto;

import com.tomtom.scoop.domain.exercise.model.entity.ExerciseLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseLevelDto {

    @Schema(description = "Exercise Name", example = "Running")
    private String exerciseName;

    @Schema(description = "Exercise Level", example = "Beginner")
    private String level;

    public static ExerciseLevelDto fromEntity(ExerciseLevel exerciseLevel) {
        return ExerciseLevelDto.builder()
                .exerciseName(exerciseLevel.getExercise())
                .level(exerciseLevel.getLevel())
                .build();
    }
}

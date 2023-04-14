package com.tomtom.scoop.domain.user.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseLevelDto {

    @Schema(description = "Exercise Name", example = "Running")
    private String exerciseName;

    @Schema(description = "Exercise Level", example = "Beginner")
    private String level;

}

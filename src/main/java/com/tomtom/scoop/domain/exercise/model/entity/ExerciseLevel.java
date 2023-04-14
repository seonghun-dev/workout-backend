package com.tomtom.scoop.domain.exercise.model.entity;

import com.tomtom.scoop.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseLevel extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exercise_level_id")
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String exercise;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String level;

}

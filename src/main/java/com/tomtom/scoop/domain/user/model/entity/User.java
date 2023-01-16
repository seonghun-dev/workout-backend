package com.tomtom.scoop.domain.user.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(columnDefinition = "VARCHAR(255)")
    private String name;

    @Column(columnDefinition = "VARCHAR(255)")
    private String phone;

    @Column(columnDefinition = "VARCHAR(255)")
    private String nickname;

    private Float rating;

    private Integer age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(columnDefinition = "VARCHAR(255)")
    private String deviceToken;

    @Column(columnDefinition = "TINYINT")
    private boolean isDeleted;

    @Column(columnDefinition = "VARCHAR(255)")
    private String profileImg;

    @OneToMany(mappedBy = "user")
    private List<UserExerciseLevel> userExerciseLevels;

    @OneToMany(mappedBy = "user")
    private List<UserLocation> userLocations;

    @OneToMany(mappedBy = "user")
    private List<UserKeyword> userKeywords;

}

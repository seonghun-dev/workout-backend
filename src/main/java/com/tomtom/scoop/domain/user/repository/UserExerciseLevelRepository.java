package com.tomtom.scoop.domain.user.repository;

import com.tomtom.scoop.domain.user.model.entity.User;
import com.tomtom.scoop.domain.user.model.entity.UserExerciseLevel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserExerciseLevelRepository extends JpaRepository<UserExerciseLevel, Long> {

    void deleteAllByUser(User user);
}

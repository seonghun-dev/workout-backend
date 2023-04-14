package com.tomtom.scoop.domain.user.repository;

import com.tomtom.scoop.domain.user.model.entity.User;
import com.tomtom.scoop.domain.user.model.entity.UserLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserLocationRepository extends JpaRepository<UserLocation, Long> {
    Optional<UserLocation> findByUser(User user);
}

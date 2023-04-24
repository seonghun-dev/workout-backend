package com.tomtom.scoop.domain.user.repository;

import com.tomtom.scoop.domain.user.model.entity.UserLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLocationRepository extends JpaRepository<UserLocation, Long> {
}

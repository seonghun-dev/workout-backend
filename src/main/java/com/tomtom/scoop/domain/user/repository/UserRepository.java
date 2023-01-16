package com.tomtom.scoop.domain.user.repository;

import com.tomtom.scoop.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {
}

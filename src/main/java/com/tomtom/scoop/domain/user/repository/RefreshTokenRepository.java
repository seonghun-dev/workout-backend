package com.tomtom.scoop.domain.user.repository;

import com.tomtom.scoop.global.common.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    Optional<RefreshToken> findByKey(String key);
}

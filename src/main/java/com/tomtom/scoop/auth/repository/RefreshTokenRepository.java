package com.tomtom.scoop.auth.repository;

import com.tomtom.scoop.auth.model.dao.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    Optional<RefreshToken> findByKey(String key);
}

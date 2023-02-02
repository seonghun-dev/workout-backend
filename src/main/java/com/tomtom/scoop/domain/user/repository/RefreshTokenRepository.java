package com.tomtom.scoop.domain.user.repository;

import com.tomtom.scoop.global.common.TokenDao;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<TokenDao, String> {
    Optional<TokenDao> findByKey(String key);
}

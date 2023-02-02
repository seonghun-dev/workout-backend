package com.tomtom.scoop.domain.user.repository;

import com.tomtom.scoop.global.common.TokenDao;
import org.springframework.data.repository.CrudRepository;

public interface LogoutAccessTokenRepository extends CrudRepository<TokenDao, String> {
}

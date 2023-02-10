package com.tomtom.scoop.domain.user.repository;

import com.tomtom.scoop.domain.user.model.dao.LogoutAccessToken;
import org.springframework.data.repository.CrudRepository;

public interface LogoutAccessTokenRepository extends CrudRepository<LogoutAccessToken, String> {
}

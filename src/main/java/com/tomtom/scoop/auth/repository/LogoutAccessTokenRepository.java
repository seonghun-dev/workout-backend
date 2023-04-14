package com.tomtom.scoop.auth.repository;

import com.tomtom.scoop.auth.model.dao.LogoutAccessToken;
import org.springframework.data.repository.CrudRepository;

public interface LogoutAccessTokenRepository extends CrudRepository<LogoutAccessToken, String> {
}

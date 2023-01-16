package com.tomtom.scoop.domain.user.service;

import com.tomtom.scoop.domain.user.model.dto.UserDto;
import com.tomtom.scoop.domain.user.model.entity.User;
import com.tomtom.scoop.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;


    @Transactional(readOnly = true)
    public UserDto.response loadUserByOauthId(String oauthId) throws UsernameNotFoundException {
        return userRepository.findByOauthId(oauthId).map(findUser -> new UserDto.response(findUser)).orElseThrow(
                () -> new IllegalArgumentException()
        );
        
    }

}

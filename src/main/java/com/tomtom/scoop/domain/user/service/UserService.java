package com.tomtom.scoop.domain.user.service;

import com.tomtom.scoop.domain.user.model.dto.UserDto;
import com.tomtom.scoop.domain.user.model.entity.User;
import com.tomtom.scoop.domain.user.repository.UserRepository;
import com.tomtom.scoop.global.security.CustomUserDetails;
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
    public CustomUserDetails loadUserByOauthId(String oauthId) throws UsernameNotFoundException {
        return userRepository.findByOauthId(oauthId).map(findUser -> new CustomUserDetails(findUser)).orElseThrow(
                () -> new IllegalArgumentException()
        );
        
    }

}

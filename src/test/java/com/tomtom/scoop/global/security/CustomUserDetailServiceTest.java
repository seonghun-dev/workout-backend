package com.tomtom.scoop.global.security;

import com.tomtom.scoop.domain.user.model.entity.User;
import com.tomtom.scoop.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomUserDetailServiceTest")
public class CustomUserDetailServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("loadUserByUsername Success Test")
    public void testSuccessLoadUserByUsername() {
        String oauthId = "testUserOauthId";
        User user = User.builder().id(1L).name("testUser").nickname("testUserNickname").oauthId("testUserOauthId").build();
        when(userRepository.findByOauthId(oauthId)).thenReturn(Optional.of(user));

        var result = customUserDetailsService.loadUserByUsername(oauthId);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testUserNickname");
        assertThat(result.getPassword()).isNull();
        assertThat(result.getAuthorities()).isNull();
        assertThat(result.isEnabled()).isFalse();
        assertThat(result.isAccountNonExpired()).isFalse();
        assertThat(result.isAccountNonLocked()).isFalse();
        assertThat(result.isCredentialsNonExpired()).isFalse();
    }


    @Test
    @DisplayName("loadUserByUsername Fail Test")
    public void testFailLoadUserByUsername() {
        String oauthId = "testUserOauthId";
        when(userRepository.findByOauthId(oauthId)).thenReturn(Optional.empty());

        Assertions.assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(oauthId)
        );
    }


}

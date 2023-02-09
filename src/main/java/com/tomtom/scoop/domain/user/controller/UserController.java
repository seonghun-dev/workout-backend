package com.tomtom.scoop.domain.user.controller;

import com.tomtom.scoop.domain.user.model.dto.request.UserJoinDto;
import com.tomtom.scoop.domain.user.model.dto.request.UserUpdateDto;
import com.tomtom.scoop.domain.user.model.dto.response.UserResponseDto;
import com.tomtom.scoop.domain.user.model.entity.User;
import com.tomtom.scoop.domain.user.service.UserService;
import com.tomtom.scoop.global.annotation.ReqUser;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    @Operation(summary = "Enter additional information when registering as a member")
    public UserResponseDto join(@ReqUser() User user, @RequestBody UserJoinDto userJoinDto) {
        return userService.join(user, userJoinDto);
    }

    @PostMapping("/update")
    @Operation(summary = "update user info")
    public UserResponseDto update(@ReqUser() User user, @RequestBody UserUpdateDto userUpdateDto) {
        return userService.update(user, userUpdateDto);
    }

    @GetMapping
    @Operation(summary = "get my info")
    public UserResponseDto me(@ReqUser() User user) {
        return userService.me(user);
    }


}

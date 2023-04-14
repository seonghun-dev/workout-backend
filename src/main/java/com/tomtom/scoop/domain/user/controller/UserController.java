package com.tomtom.scoop.domain.user.controller;

import com.tomtom.scoop.domain.common.model.ResponseDto;
import com.tomtom.scoop.domain.user.model.dto.UserLocationDto;
import com.tomtom.scoop.domain.user.model.dto.request.UserJoinDto;
import com.tomtom.scoop.domain.user.model.dto.request.UserUpdateDto;
import com.tomtom.scoop.domain.user.model.dto.response.UserResponseDto;
import com.tomtom.scoop.domain.user.model.entity.User;
import com.tomtom.scoop.domain.user.service.UserService;
import com.tomtom.scoop.global.annotation.ReqUser;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(value = "/join")
    @Operation(summary = "Enter additional information when registering as a member")
    public ResponseEntity<UserResponseDto> join(@ReqUser() User user, @RequestBody UserJoinDto userJoinDto) {
        var response = userService.join(user, userJoinDto);
        return ResponseDto.created(response);
    }

    @PostMapping("/update")
    @Operation(summary = "update user info")
    public ResponseEntity<UserResponseDto> update(@ReqUser() User user, @RequestBody UserUpdateDto userUpdateDto) {
        var response = userService.update(user, userUpdateDto);
        return ResponseDto.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get user Information")
    public ResponseEntity<UserResponseDto> me(@ReqUser() User user) {
        var response = userService.me(user);
        return ResponseDto.ok(response);
    }

    @PatchMapping("/location")
    @Operation(summary = "update user location info")
    public ResponseEntity<UserResponseDto> updateUserLocation(@ReqUser User user, @RequestBody UserLocationDto userLocationDto) {
        var response = userService.updateUserLocation(user, userLocationDto);
        return ResponseDto.ok(response);
    }


}

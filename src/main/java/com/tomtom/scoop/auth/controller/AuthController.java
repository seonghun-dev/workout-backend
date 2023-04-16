package com.tomtom.scoop.auth.controller;

import com.tomtom.scoop.auth.model.TokenDto;
import com.tomtom.scoop.auth.service.AuthService;
import com.tomtom.scoop.domain.common.model.ResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/reissue")
    public ResponseEntity<TokenDto> reissue(HttpServletRequest request) {
        var response = authService.reissue(request);
        return ResponseDto.ok(response);
    }
}

package com.tomtom.scoop.auth.controller;

import com.tomtom.scoop.auth.model.TokenDto;
import com.tomtom.scoop.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @GetMapping("/reissue")
    public ResponseEntity<TokenDto> reissue(HttpServletRequest request, HttpServletResponse response) throws IOException {
        TokenDto token = authService.reissue(request, response);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(token);
    }
}

package com.tomtom.scoop.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tomtom.scoop.global.exception.ErrorCode;
import com.tomtom.scoop.global.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setContentType("application/json");
        response.setStatus(ErrorCode.INVALID_TOKEN.getStatus().value());
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        response.getWriter().write(mapper.writeValueAsString(
                ErrorResponse.builder()
                        .status(ErrorCode.INVALID_TOKEN.getStatus().value())
                        .code(ErrorCode.INVALID_TOKEN.getCode())
                        .message(ErrorCode.INVALID_TOKEN.getMessage())
                        .build())
        );
    }
}

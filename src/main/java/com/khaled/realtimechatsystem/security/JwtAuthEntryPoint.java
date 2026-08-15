package com.khaled.realtimechatsystem.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khaled.realtimechatsystem.handler.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.khaled.realtimechatsystem.exception.ErrorCode.AUTH_REQUIRED;

@Component
@RequiredArgsConstructor
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            @NonNull final HttpServletRequest request,
            @NonNull final HttpServletResponse response,
            @NonNull final AuthenticationException authException
    ) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponse body = (ErrorResponse) request.getAttribute("security.error");
        if (body == null) {
            body = ErrorResponse.builder()
                    .code(AUTH_REQUIRED.getCode())
                    .message(AUTH_REQUIRED.getDefaultMessage())
                    .build();
        }

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}

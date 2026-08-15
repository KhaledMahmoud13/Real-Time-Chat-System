package com.khaled.realtimechatsystem.ws;

import com.khaled.realtimechatsystem.exception.BusinessException;
import com.khaled.realtimechatsystem.security.JwtService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtHandshakeInterceptor implements HandshakeInterceptor {
    // TODO: USER DETAILS SERVICES TO CHECK IF THE USER EXISITS
    private final JwtService jwtService;

    @Override
    public boolean beforeHandshake(
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response,
            @NonNull WebSocketHandler wsHandler,
            @NonNull Map<String, Object> attributes
    ) {
        String token = UriComponentsBuilder.fromUri(request.getURI())
                .build()
                .getQueryParams()
                .getFirst("token");

        if (token == null || token.isBlank()) {
            response.setStatusCode(UNAUTHORIZED);
            return false;
        }

        try {
            String username = jwtService.extractUsername(token);

            if (jwtService.isTokenExpired(token)) {
                response.setStatusCode(UNAUTHORIZED);
                return false;
            }

            attributes.put("username", username);
            return true;
        } catch (BusinessException e) {
            log.warn("WebSocket handshake rejected: {}", e.getMessage());
            response.setStatusCode(UNAUTHORIZED);
            return false;
        }
    }

    @Override
    public void afterHandshake(
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response,
            @NonNull WebSocketHandler wsHandler,
            @Nullable Exception exception
    ) {

    }
}

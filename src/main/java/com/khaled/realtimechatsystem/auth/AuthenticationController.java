package com.khaled.realtimechatsystem.auth;

import com.khaled.realtimechatsystem.auth.request.AuthenticationRequest;
import com.khaled.realtimechatsystem.auth.request.RefreshRequest;
import com.khaled.realtimechatsystem.auth.request.RegistrationRequest;
import com.khaled.realtimechatsystem.auth.response.AuthenticationResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication API")
public class AuthenticationController {
    private final AuthenticationService service;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @Valid
            @RequestBody final AuthenticationRequest request
    ) {
        return ResponseEntity.ok(this.service.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @Valid
            @RequestBody final RegistrationRequest request
    ) {
        this.service.register(request);
        return ResponseEntity.status(CREATED).build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(@RequestBody final RefreshRequest request) {
        return ResponseEntity.ok(this.service.refreshToken(request));
    }
}

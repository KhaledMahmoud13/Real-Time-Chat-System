package com.khaled.realtimechatsystem.user;

import com.khaled.realtimechatsystem.auth.request.RegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserMapper {
    private final PasswordEncoder passwordEncoder;

    public User toUser(final RegistrationRequest request) {
        return User.builder()
                .fullName(request.fullName())
                .username(request.username())
                .email(request.email())
                .password(this.passwordEncoder.encode(request.password()))
                .enabled(true)
                .accountLocked(false)
                .credentialsExpired(false)
                .emailVerified(false)
                .build();
    }
}

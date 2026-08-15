package com.khaled.realtimechatsystem.auth.impl;

import com.khaled.realtimechatsystem.auth.AuthenticationService;
import com.khaled.realtimechatsystem.auth.request.AuthenticationRequest;
import com.khaled.realtimechatsystem.auth.request.RefreshRequest;
import com.khaled.realtimechatsystem.auth.request.RegistrationRequest;
import com.khaled.realtimechatsystem.auth.response.AuthenticationResponse;
import com.khaled.realtimechatsystem.exception.BusinessException;
import com.khaled.realtimechatsystem.security.JwtService;
import com.khaled.realtimechatsystem.user.User;
import com.khaled.realtimechatsystem.user.UserMapper;
import com.khaled.realtimechatsystem.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.khaled.realtimechatsystem.exception.ErrorCode.EMAIL_ALREADY_EXISTS;
import static com.khaled.realtimechatsystem.exception.ErrorCode.USERNAME_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public AuthenticationResponse login(final AuthenticationRequest request) {
        final Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );
        final User user = (User) authentication.getPrincipal();
        final String accessToken = this.jwtService.generateAccessToken(user.getUsername());
        final String refreshToken = this.jwtService.generateRefreshToken(user.getUsername());

        return new AuthenticationResponse(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public void register(final RegistrationRequest request) {
        checkUserEmail(request.email());
        checkUserUsername(request.username());

        final User user = this.userMapper.toUser(request);
        log.debug("Saving user {}", user);
        this.userRepository.save(user);
    }

    @Override
    public AuthenticationResponse refreshToken(final RefreshRequest request) {
        final String newAccessToken = this.jwtService.refreshAccessToken(request.refreshToken());
        return new AuthenticationResponse(newAccessToken, request.refreshToken());
    }

    private void checkUserEmail(final String email) {
        final boolean emailExists = this.userRepository.existsByEmailIgnoreCase(email);
        if (emailExists) {
            throw new BusinessException(EMAIL_ALREADY_EXISTS);
        }
    }

    private void checkUserUsername(final String username) {
        final boolean usernameExists = this.userRepository.existsByUsernameIgnoreCase(username);
        if (usernameExists) {
            throw new BusinessException(USERNAME_ALREADY_EXISTS);
        }
    }
}

package com.khaled.realtimechatsystem.auth.impl;

import com.khaled.realtimechatsystem.auth.request.AuthenticationRequest;
import com.khaled.realtimechatsystem.auth.request.RefreshRequest;
import com.khaled.realtimechatsystem.auth.request.RegistrationRequest;
import com.khaled.realtimechatsystem.auth.response.AuthenticationResponse;
import com.khaled.realtimechatsystem.exception.BusinessException;
import com.khaled.realtimechatsystem.security.JwtService;
import com.khaled.realtimechatsystem.user.User;
import com.khaled.realtimechatsystem.user.UserMapper;
import com.khaled.realtimechatsystem.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import static com.khaled.realtimechatsystem.exception.ErrorCode.EMAIL_ALREADY_EXISTS;
import static com.khaled.realtimechatsystem.exception.ErrorCode.USERNAME_ALREADY_EXISTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationServiceImpl Unit Tests")
class AuthenticationServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @BeforeEach
    void setUp() {
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Should return token")
        void shouldReturnToken() {
            // Given
            AuthenticationRequest request = new AuthenticationRequest("username", "password");

            User user = User.builder().username("username").build();

            Authentication authentication = mock(Authentication.class);

            when(authenticationManager.authenticate(any())).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(user);
            when(jwtService.generateAccessToken("username")).thenReturn("access-token");
            when(jwtService.generateRefreshToken("username")).thenReturn("refresh-token");

            // When
            AuthenticationResponse response = authenticationService.login(request);

            // Then
            assertEquals("access-token", response.accessToken());
            assertEquals("refresh-token", response.refreshToken());

        }
    }

    @Nested
    @DisplayName("Register Tests")
    class RegisterTests {
        @Test
        @DisplayName("Should save user when email and username are unique")
        void shouldSaveUserWhenEmailAndUsernameAreUnique() {
            // Given
            RegistrationRequest request = mock(RegistrationRequest.class);

            when(request.email()).thenReturn("john.doe@example.com");
            when(request.username()).thenReturn("john_doe");

            User user = User.builder().build();

            when(userRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
            when(userRepository.existsByUsernameIgnoreCase(anyString())).thenReturn(false);
            when(userMapper.toUser(request)).thenReturn(user);

            // When
            authenticationService.register(request);

            // Then
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("Should throw exception when email is already registered")
        void shouldThrowExceptionWhenEmailIsAlreadyRegistered() {
            // Given
            RegistrationRequest request = mock(RegistrationRequest.class);
            when(request.email()).thenReturn("test@test.com");
            when(userRepository.existsByEmailIgnoreCase("test@test.com")).thenReturn(true);

            // When & Then
            BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> authenticationService.register(request)
                    );
            assertEquals(EMAIL_ALREADY_EXISTS, exception.getErrorCode());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when username is already registered")
        void shouldThrowExceptionWhenUsernameIsAlreadyRegistered() {
            // Given
            RegistrationRequest request = mock(RegistrationRequest.class);
            when(request.email()).thenReturn("test@test.com");
            when(request.username()).thenReturn("khaled");
            when(userRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
            when(userRepository.existsByUsernameIgnoreCase("khaled")).thenReturn(true);

            // When & Then
            BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> authenticationService.register(request)
                    );
            assertEquals(USERNAME_ALREADY_EXISTS, exception.getErrorCode());
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Refresh Token Tests")
    class RefreshTokenTests {
        @Test
        @DisplayName("Should return new access token")
        void shouldReturnNewAccessToken() {
            // Given
            RefreshRequest request = new RefreshRequest("refresh-token");

            when(jwtService.refreshAccessToken("refresh-token")).thenReturn("new-access-token");

            // When
            AuthenticationResponse response = authenticationService.refreshToken(request);

            // Then
            assertEquals("new-access-token", response.accessToken());
            assertEquals("refresh-token", response.refreshToken());
        }
    }
}
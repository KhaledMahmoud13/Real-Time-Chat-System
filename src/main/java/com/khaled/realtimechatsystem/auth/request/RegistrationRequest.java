package com.khaled.realtimechatsystem.auth.request;

import com.khaled.realtimechatsystem.validation.NonDisposableEmail;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(

        @NotBlank(message = "VALIDATION.REGISTRATION.FIRSTNAME.NOT_BLANK")
        @Size(min = 1, max = 50, message = "VALIDATION.REGISTRATION.FIRSTNAME.SIZE")
        @Pattern(regexp = "^[\\p{L} '-]+$", message = "VALIDATION.REGISTRATION.FIRST_NAME.PATTERN")
        @Schema(example = "John Doe")
        String fullName,

        @NotBlank(message = "VALIDATION.REGISTRATION.USERNAME.BLANK")
        @Size(min = 3, max = 30, message = "VALIDATION.REGISTRATION.USERNAME.SIZE")
        @Pattern(
                regexp = "^(?=.{3,30}$)(?!.*[._]{2})[a-zA-Z0-9](?:[a-zA-Z0-9._]*[a-zA-Z0-9])?$",
                message = "VALIDATION.REGISTRATION.USERNAME.PATTERN"
        )
        @Schema(example = "john_doe")
        String username,

        @NotBlank(message = "VALIDATION.REGISTRATION.EMAIL.BLANK")
        @Email(message = "VALIDATION.REGISTRATION.EMAIL.FORMAT")
        @NonDisposableEmail(message = "VALIDATION.REGISTRATION.EMAIL.DISPOSABLE")
        @Schema(example = "john.doe@example.com")
        String email,

        @NotBlank(message = "VALIDATION.REGISTRATION.PASSWORD.BLANK")
        @Size(min = 8, max = 72, message = "VALIDATION.REGISTRATION.PASSWORD.SIZE")
        @Pattern(
                regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{8,}$",
                message = "VALIDATION.REGISTRATION.PASSWORD.WEAK"
        )
        @Schema(example = "password1")
        String password
) {
}

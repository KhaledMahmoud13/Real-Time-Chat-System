package com.khaled.realtimechatsystem.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequest(

        @NotBlank(message = "VALIDATION.AUTHENTICATION.USERNAME.NOT_BLANK")
        @Schema(example = "john_doe")
        String username,

        @NotBlank(message = "VALIDATION.AUTHENTICATION.PASSWORD.NOT_BLANK")
        @Schema(example = "password1")
        String password

) {
}

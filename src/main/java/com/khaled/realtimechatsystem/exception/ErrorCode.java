package com.khaled.realtimechatsystem.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ErrorCode {

    INVALID_TOKEN("INVALID_TOKEN", "Invalid token", UNAUTHORIZED),
    TOKEN_EXPIRED("TOKEN_EXPIRED", "Token expired", UNAUTHORIZED),
    INVALID_TOKEN_TYPE("INVALID_TOKEN_TYPE", "Invalid token type", UNAUTHORIZED),
    AUTH_REQUIRED("UNAUTHORIZED", "Authentication is required to access this resource", UNAUTHORIZED),
    USERNAME_NOT_FOUND("USERNAME_NOT_FOUND", "Cannot find user with the provided username", NOT_FOUND),
    INTERNAL_EXCEPTION("INTERNAL_EXCEPTION", "An internal exception occurred, please try again or contact the admin", INTERNAL_SERVER_ERROR),
    BAD_CREDENTIALS("BAD_CREDENTIALS", "Username and / or password is incorrect", UNAUTHORIZED),
    EMAIL_ALREADY_EXISTS("ERR_EMAIL_EXISTS", "Email already exists", CONFLICT),
    USERNAME_ALREADY_EXISTS("ERR_USERNAME_EXISTS", "Username already exists", CONFLICT),
    ;

    private final String code;
    private final String defaultMessage;
    private final HttpStatus status;

    ErrorCode(String code, String defaultMessage, HttpStatus status) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.status = status;
    }
}

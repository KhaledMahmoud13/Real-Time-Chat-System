package com.khaled.realtimechatsystem.auth;

import com.khaled.realtimechatsystem.auth.request.AuthenticationRequest;
import com.khaled.realtimechatsystem.auth.request.RefreshRequest;
import com.khaled.realtimechatsystem.auth.request.RegistrationRequest;
import com.khaled.realtimechatsystem.auth.response.AuthenticationResponse;

public interface AuthenticationService {

    AuthenticationResponse login(AuthenticationRequest request);

    void register(RegistrationRequest request);

    AuthenticationResponse refreshToken(RefreshRequest request);
}

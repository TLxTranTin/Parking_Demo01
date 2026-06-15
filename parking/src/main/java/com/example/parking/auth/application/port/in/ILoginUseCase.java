package com.example.parking.auth.application.port.in;

import com.example.parking.auth.adapter.in.web.dto.LoginRequest;
import com.example.parking.auth.adapter.in.web.dto.LoginResponse;

public interface ILoginUseCase {

    LoginResponse login(LoginRequest request);
}
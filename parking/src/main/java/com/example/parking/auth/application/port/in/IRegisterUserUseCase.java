package com.example.parking.auth.application.port.in;

import com.example.parking.auth.adapter.in.web.dto.RegisterRequest;
import com.example.parking.auth.adapter.in.web.dto.RegisterResponse;

public interface IRegisterUserUseCase {

    RegisterResponse register(RegisterRequest request);
}
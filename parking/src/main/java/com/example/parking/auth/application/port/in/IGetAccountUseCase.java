package com.example.parking.auth.application.port.in;

import com.example.parking.auth.domain.model.AuthUser;

import java.util.List;

public interface IGetAccountUseCase {

    List<AuthUser> getAllAccounts();
}
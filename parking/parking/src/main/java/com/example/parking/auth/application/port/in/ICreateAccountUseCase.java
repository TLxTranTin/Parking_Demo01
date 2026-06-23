package com.example.parking.auth.application.port.in;

import com.example.parking.auth.domain.model.AuthUser;
import com.example.parking.auth.domain.model.Role;

public interface ICreateAccountUseCase {

    AuthUser createAccount(String username, String password, Role role);
}
package com.example.parking.auth.application.port.in;

import com.example.parking.auth.domain.model.AuthUser;

public interface IUnlockAccountUseCase {

    AuthUser unlockAccount(Long accountId);
}
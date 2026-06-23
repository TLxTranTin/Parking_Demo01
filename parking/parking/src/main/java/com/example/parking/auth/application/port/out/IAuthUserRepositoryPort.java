package com.example.parking.auth.application.port.out;

import com.example.parking.auth.domain.model.AuthUser;

import java.util.List;
import java.util.Optional;

public interface IAuthUserRepositoryPort {

    boolean existsByUsername(String username);

    Optional<AuthUser> findByUsername(String username);

    Optional<AuthUser> findById(Long id);

    List<AuthUser> findAll();

    AuthUser save(AuthUser user);
}
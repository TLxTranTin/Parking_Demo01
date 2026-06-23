package com.example.parking.auth.adapter.out.persistence;

import com.example.parking.auth.application.port.out.IAuthUserRepositoryPort;
import com.example.parking.auth.domain.model.AuthUser;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AuthUserPersistenceAdapter implements IAuthUserRepositoryPort {

    private final ISpringDataAuthUserRepository springDataAuthUserRepository;

    public AuthUserPersistenceAdapter(ISpringDataAuthUserRepository springDataAuthUserRepository) {
        this.springDataAuthUserRepository = springDataAuthUserRepository;
    }

    @Override
    public boolean existsByUsername(String username) {
        return springDataAuthUserRepository.existsByUsername(username);
    }

    @Override
    public Optional<AuthUser> findByUsername(String username) {
        return springDataAuthUserRepository.findByUsername(username)
                .map(this::toDomain);
    }

    @Override
    public Optional<AuthUser> findById(Long id) {
        return springDataAuthUserRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public List<AuthUser> findAll() {
        return springDataAuthUserRepository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public AuthUser save(AuthUser user) {
        AuthUserEntity entity = toEntity(user);
        AuthUserEntity savedEntity = springDataAuthUserRepository.save(entity);
        return toDomain(savedEntity);
    }

    private AuthUser toDomain(AuthUserEntity entity) {
        return new AuthUser(
                entity.getId(),
                entity.getUsername(),
                entity.getPasswordHash(),
                entity.getRole(),
                entity.isEnabled()
        );
    }

    private AuthUserEntity toEntity(AuthUser user) {
        return new AuthUserEntity(
                user.getId(),
                user.getUsername(),
                user.getPasswordHash(),
                user.getRole(),
                user.isEnabled()
        );
    }
}
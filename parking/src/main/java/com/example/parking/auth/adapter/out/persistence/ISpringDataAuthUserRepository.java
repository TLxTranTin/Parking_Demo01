package com.example.parking.auth.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ISpringDataAuthUserRepository extends JpaRepository<AuthUserEntity, Long> {

    boolean existsByUsername(String username);

    Optional<AuthUserEntity> findByUsername(String username);
}
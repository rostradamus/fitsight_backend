package com.rostradamus.wologbackend.repository;

import com.rostradamus.wologbackend.model.UnsafeUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UnsafeUserRepository extends JpaRepository<UnsafeUser, Long> {
  Optional<UnsafeUser> findByEmail(String email);

  Boolean existsByEmail(String email);
}

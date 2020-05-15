package com.rostradamus.wologbackend.repository;

import com.rostradamus.wologbackend.model.ERole;
import com.rostradamus.wologbackend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(ERole name);
}

package com.rostradamus.fitsight.backend.repository;

import com.rostradamus.fitsight.backend.model.ERole;
import com.rostradamus.fitsight.backend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(ERole name);
}

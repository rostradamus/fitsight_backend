package com.rostradamus.fitsight.backend.controller;

import com.rostradamus.fitsight.backend.controller.payload.request.LoginRequest;
import com.rostradamus.fitsight.backend.controller.payload.request.SignupRequest;
import com.rostradamus.fitsight.backend.controller.payload.response.JwtResponse;
import com.rostradamus.fitsight.backend.repository.RoleRepository;
import com.rostradamus.fitsight.backend.model.ERole;
import com.rostradamus.fitsight.backend.model.Role;
import com.rostradamus.fitsight.backend.model.UnsafeUser;
import com.rostradamus.fitsight.backend.repository.UnsafeUserRepository;
import com.rostradamus.fitsight.backend.security.jwt.JwtUtils;
import com.rostradamus.fitsight.backend.security.service.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UnsafeUserRepository unsafeUserRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Autowired
  JwtUtils jwtUtils;

  @PostMapping
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    Authentication authentication = authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
    );

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    List<String> roles = userDetails.getAuthorities().stream()
      .map(GrantedAuthority::getAuthority)
      .collect(Collectors.toList());
    return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getEmail(), roles));
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
    if (unsafeUserRepository.existsByEmail(signupRequest.getEmail())) {
      return ResponseEntity.badRequest().build();
    }

    UnsafeUser user = new UnsafeUser(
      signupRequest.getEmail(),
      passwordEncoder.encode(signupRequest.getPassword()),
      signupRequest.getFirstName(),
      signupRequest.getLastName()
    );

    Set<Role> roles = new HashSet<>();

    Role userRole = roleRepository.findByName(ERole.ROLE_USER)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));

    roles.add(userRole);

    user.setRoles(roles);

    return ResponseEntity.ok(unsafeUserRepository.save(user));
  }
}

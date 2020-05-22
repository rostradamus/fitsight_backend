package com.rostradamus.wologbackend.controller;

import com.rostradamus.wologbackend.controller.payload.request.LoginRequest;
import com.rostradamus.wologbackend.controller.payload.request.SignupRequest;
import com.rostradamus.wologbackend.controller.payload.response.JwtResponse;
import com.rostradamus.wologbackend.model.ERole;
import com.rostradamus.wologbackend.model.Role;
import com.rostradamus.wologbackend.model.User;
import com.rostradamus.wologbackend.repository.RoleRepository;
import com.rostradamus.wologbackend.repository.UserRepository;
import com.rostradamus.wologbackend.security.SecurityConstants;
import com.rostradamus.wologbackend.security.jwt.JwtUtils;
import com.rostradamus.wologbackend.security.service.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
  UserRepository userRepository;

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
//    String jwt = jwtUtils.generateJwtToken(authentication);
//    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//    List<String> roles = userDetails.getAuthorities().stream()
//      .map(GrantedAuthority::getAuthority)
//      .collect(Collectors.toList());
//    System.out.println(jwt);
    return ResponseEntity.ok().build();
//      .header(SecurityConstants.TOKEN_HEADER, SecurityConstants.TOKEN_PREFIX + jwt)
//      .body(new JwtResponse(jwt, userDetails.getId(), userDetails.getEmail(), roles));
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
    if (userRepository.existsByEmail(signupRequest.getEmail())) {
      return ResponseEntity.badRequest().build();
    }

    User user = new User(
      signupRequest.getEmail(),
      passwordEncoder.encode(signupRequest.getPassword()),
      signupRequest.getFirstName(),
      signupRequest.getLastName()
    );

    Set<Role> roles = new HashSet<>();

    Optional<Role> userRole = roleRepository.findByName(ERole.ROLE_USER);
    if (userRole.isEmpty()) {
      log.info("User Role does NOT Exist");
      return ResponseEntity.badRequest().build();
    }

    roles.add(userRole.get());

    user.setRoles(roles);

    return ResponseEntity.ok(userRepository.save(user));
  }
}

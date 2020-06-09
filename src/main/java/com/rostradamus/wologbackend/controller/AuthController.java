package com.rostradamus.wologbackend.controller;

import com.rostradamus.wologbackend.controller.payload.request.LoginRequest;
import com.rostradamus.wologbackend.controller.payload.request.SignupRequest;
import com.rostradamus.wologbackend.controller.payload.response.JwtResponse;
import com.rostradamus.wologbackend.model.ERole;
import com.rostradamus.wologbackend.model.Role;
import com.rostradamus.wologbackend.model.UnsafeUser;
import com.rostradamus.wologbackend.model.User;
import com.rostradamus.wologbackend.repository.RoleRepository;
import com.rostradamus.wologbackend.repository.UnsafeUserRepository;
import com.rostradamus.wologbackend.repository.UserRepository;
import com.rostradamus.wologbackend.security.jwt.JwtUtils;
import com.rostradamus.wologbackend.security.refreshtoken.RefreshToken;
import com.rostradamus.wologbackend.security.refreshtoken.RefreshTokenRepository;
import com.rostradamus.wologbackend.security.service.UserDetailsImpl;
import com.rostradamus.wologbackend.security.service.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Nullable;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;
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
  UserRepository userRepository;

  @Autowired
  UserDetailsServiceImpl userDetailsService;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  RefreshTokenRepository refreshTokenRepository;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Autowired
  JwtUtils jwtUtils;

  @PostMapping
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
    try {
      Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
      );
      SecurityContextHolder.getContext().setAuthentication(authentication);
      UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
      String jwt = jwtUtils.generateJwtToken(userDetails);
      List<String> roles = userDetails.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());

      this.processRefreshToken(response, userDetails.getUsername());
      User user = userRepository.findById(userDetails.getId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));

      return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), roles, user));
    } catch (AuthenticationException e) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
  }

  @DeleteMapping
  public ResponseEntity<?> logout(@Nullable @CookieValue(value = "refresh_token") String refreshTokenId, HttpServletResponse response) {
    if (refreshTokenId != null) {
      refreshTokenRepository.deleteById(refreshTokenId);
      Cookie refreshTokenCookie = new Cookie("refresh_token", null);
      refreshTokenCookie.setMaxAge(0); // immediately expire
      refreshTokenCookie.setPath("/");
      response.addCookie(refreshTokenCookie);
    }
    return ResponseEntity.ok().build();
  }

  @PostMapping("/refresh_token")
  public ResponseEntity<?> refreshToken(@Nullable @CookieValue(value = "refresh_token") String refreshTokenId,
                                        HttpServletRequest request, HttpServletResponse response) {
    if (refreshTokenId != null) {
      RefreshToken existingToken = refreshTokenRepository.findById(refreshTokenId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

      String username = existingToken.getUsername();
      UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(username);
      UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
        userDetails, null, userDetails.getAuthorities());
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

      SecurityContextHolder.getContext().setAuthentication(authentication);

      String jwt = jwtUtils.generateJwtToken(userDetails);
      List<String> roles = userDetails.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());

      refreshTokenRepository.deleteById(refreshTokenId);
      this.processRefreshToken(response, username);
      User user = userRepository.findById(userDetails.getId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));

      return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), roles, user));
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
    if (unsafeUserRepository.existsByEmail(signupRequest.getEmail())) {
      return ResponseEntity.unprocessableEntity().build();
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

    unsafeUserRepository.save(user);
    return ResponseEntity.ok().build();
  }

  private void processRefreshToken(HttpServletResponse response, String username) {
    RefreshToken refreshToken = refreshTokenRepository.save(new RefreshToken(username));
    Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken.getId());
    refreshTokenCookie.setMaxAge(1 * 24 * 60 * 60); // expires in 7 days
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setPath("/");
//    refreshTokenCookie.setSecure(true); // TODO: Enable this when deploy to production is ready
    response.addCookie(refreshTokenCookie);
  }
}

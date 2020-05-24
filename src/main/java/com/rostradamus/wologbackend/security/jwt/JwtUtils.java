package com.rostradamus.wologbackend.security.jwt;

import com.rostradamus.wologbackend.model.User;
import com.rostradamus.wologbackend.security.SecurityConstants;
import com.rostradamus.wologbackend.security.service.UserDetailsImpl;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
public class JwtUtils {

  @Value("${rostradamus.app.jwtSecret}")
  private String jwtSecret;

  @Value("${rostradamus.app.jwtExpirationMs}")
  private int jwtExpirationMs;

  public String generateJwtToken(Authentication authentication) {
    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

    return generateJwtToken(userPrincipal.getUsername());
  }

  public String generateJwtToken(UserDetails userDetails) {
    return this.generateJwtToken(userDetails.getUsername());
  }

  private String generateJwtToken(String username) {
    return Jwts.builder()
      .setId(UUID.randomUUID().toString())
      .setIssuer(SecurityConstants.TOKEN_ISSUER)
      .setAudience(SecurityConstants.TOKEN_AUDIENCE)
      .setSubject(username)
      .setIssuedAt(new Date())
      .setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
      .signWith(SignatureAlgorithm.HS512, jwtSecret)
      .compact();
  }

  public String getUsernameFromJwtToken(String token) {
    return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
  }

  public boolean validateJwtToken(String authToken) {
    try {
      Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
      return true;
    } catch (SignatureException e) {
      log.error("Invalid JWT signature: {}", e.getMessage());
    } catch (MalformedJwtException e) {
      log.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      log.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      log.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      log.error("JWT claims string is empty: {}", e.getMessage());
    }

    return false;
  }

  public String parseJwtFrom(String header) {
    if (StringUtils.hasText(header) && header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
      return header.replace(SecurityConstants.TOKEN_PREFIX, "");
    }

    return null;
  }
}

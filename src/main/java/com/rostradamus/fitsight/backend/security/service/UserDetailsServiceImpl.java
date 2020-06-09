package com.rostradamus.fitsight.backend.security.service;

import com.rostradamus.fitsight.backend.model.UnsafeUser;
import com.rostradamus.fitsight.backend.repository.UnsafeUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  @Autowired
  UnsafeUserRepository unsafeUserRepository;

  public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
    UnsafeUser user = unsafeUserRepository.findByEmail(email)
      .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));
    return UserDetailsImpl.build(user);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return loadUserByEmail(username);
  }
}

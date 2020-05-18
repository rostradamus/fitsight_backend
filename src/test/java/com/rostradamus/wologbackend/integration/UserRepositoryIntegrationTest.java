package com.rostradamus.wologbackend.integration;

import com.rostradamus.wologbackend.exception.UserNotFoundException;
import com.rostradamus.wologbackend.model.User;
import com.rostradamus.wologbackend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class UserRepositoryIntegrationTest {
  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private UserRepository userRepository;

  @Test
  public void whenFindByEmail_thenReturnUser() {
    User testUser = new User("test@example.com", "TestPassword12#", "Test", "User");
    entityManager.persist(testUser);
    entityManager.flush();

    try {
      User found = userRepository.findByEmail("test@example.com").orElseThrow(UserNotFoundException::new);
      assertThat(found).isEqualTo(testUser);
    } catch (UserNotFoundException e) {
      fail();
    }

    assertThatThrownBy(() -> userRepository.findByEmail("test_non_exist@example.com").orElseThrow(UserNotFoundException::new))
      .isInstanceOf(UserNotFoundException.class);
  }
}

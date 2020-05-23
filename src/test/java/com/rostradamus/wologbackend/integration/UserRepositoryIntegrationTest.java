package com.rostradamus.wologbackend.integration;

import com.rostradamus.wologbackend.exception.UserNotFoundException;
import com.rostradamus.wologbackend.model.UnsafeUser;
import com.rostradamus.wologbackend.repository.UnsafeUserRepository;
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
  private UnsafeUserRepository unsafeUserRepository;

  @Test
  public void whenFindByEmail_thenReturnUser() {
    UnsafeUser testUser = new UnsafeUser("test@example.com", "TestPassword12#", "Test", "User");
    entityManager.persist(testUser);
    entityManager.flush();

    try {
      UnsafeUser found = unsafeUserRepository.findByEmail("test@example.com").orElseThrow(UserNotFoundException::new);
      assertThat(found).isEqualTo(testUser);
    } catch (UserNotFoundException e) {
      fail();
    }

    assertThatThrownBy(() -> unsafeUserRepository.findByEmail("test_non_exist@example.com").orElseThrow(UserNotFoundException::new))
      .isInstanceOf(UserNotFoundException.class);
  }
}

package com.rostradamus.wologbackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(
  locations = "classpath:application-integrationtest.properties")
@SpringBootTest
class WologBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}

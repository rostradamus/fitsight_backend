package com.rostradamus.fitsight.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(
  locations = "classpath:application-integrationtest.properties")
@SpringBootTest
class BackendApplicationTests {

	@Test
	void contextLoads() {
	}

}

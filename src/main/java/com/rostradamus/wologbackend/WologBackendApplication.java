package com.rostradamus.wologbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class WologBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(WologBackendApplication.class, args);
	}

}

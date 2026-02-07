package com.bintang.jwt.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class SpringbootJwtAuthApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootJwtAuthApiApplication.class, args);
	}

}

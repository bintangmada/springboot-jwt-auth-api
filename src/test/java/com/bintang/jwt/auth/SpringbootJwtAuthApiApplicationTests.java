package com.bintang.jwt.auth;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("Context load fails without required environment variables (DB, JWT_SECRET, etc)")
@SpringBootTest
class SpringbootJwtAuthApiApplicationTests {

	@Test
	void contextLoads() {
	}

}

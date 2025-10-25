package com.ecommerce.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class BackendApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	public void generatePasswordHash() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String rawPassword = "123";
		String encodedPassword = encoder.encode(rawPassword);
		// Print the hash to your console
		System.out.println("The BCrypt hash for '" + rawPassword + "' is: " + encodedPassword);
		// Optional: Verify the hash works
		System.out.println("Verification Result: " + encoder.matches(rawPassword, encodedPassword));
	}
}
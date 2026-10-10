package com.finlyticsltd.finlytics.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class JwtServiceTest {

	private static final String SECRET = "test-secret-key-that-is-at-least-32-characters-long";

	private final JwtService jwtService = new JwtService(SECRET, 3600000);

	@Test
	void generatedToken_isValidAndContainsUsername() {
		String token = jwtService.generateToken("rahul");

		assertThat(jwtService.isTokenValid(token)).isTrue();
		assertThat(jwtService.extractUsername(token)).isEqualTo("rahul");
	}

	@Test
	void tamperedToken_isInvalid() {
		String token = jwtService.generateToken("rahul");
		String tampered = token.substring(0, token.length() - 2) + "xx";

		assertThat(jwtService.isTokenValid(tampered)).isFalse();
	}

	@Test
	void expiredToken_isInvalid() {
		JwtService shortLived = new JwtService(SECRET, -1000);
		String token = shortLived.generateToken("rahul");

		assertThat(shortLived.isTokenValid(token)).isFalse();
	}

	@Test
	void tokenSignedWithDifferentSecret_isInvalid() {
		JwtService other = new JwtService("another-secret-key-that-is-also-32-chars-long", 3600000);
		String token = other.generateToken("rahul");

		assertThat(jwtService.isTokenValid(token)).isFalse();
	}
}
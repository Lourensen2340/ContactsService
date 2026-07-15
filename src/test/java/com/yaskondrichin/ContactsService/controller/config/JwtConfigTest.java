package com.yaskondrichin.ContactsService.controller.config;

import com.yaskondrichin.ContactsService.config.JwtConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(classes = JwtConfig.class)
// Задаем тестовый секретный ключ (минимум 256 бит / 32 символа для HMAC-SHA256)
@TestPropertySource(properties = {
        "jwt.secret=my-super-secret-key-that-is-at-least-256-bits-long"
})
class JwtConfigTest {

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Test
    @DisplayName("Бины JwtEncoder и JwtDecoder должны быть успешно созданы в контексте")
    void beansShouldBeCreated() {
        assertThat(jwtEncoder).isNotNull();
        assertThat(jwtDecoder).isNotNull();
    }

    @Test
    @DisplayName("JwtEncoder и JwtDecoder должны успешно работать в паре (кодирование и декодирование)")
    void encoderAndDecoderShouldWorkTogether() {
        // Given - создаем параметры токена (claims)
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("http://localhost:8080")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject("test-user")
                .claim("roles", "USER")
                .build();

        JwsHeader header = JwsHeader.with(() -> "HS256").build();
        JwtEncoderParameters parameters = JwtEncoderParameters.from(header, claims);

        // When - кодируем токен
        String tokenValue = assertDoesNotThrow(() -> jwtEncoder.encode(parameters).getTokenValue());
        assertThat(tokenValue).isNotBlank();

        // Then - декодируем токен и проверяем содержимое
        Jwt decodedJwt = assertDoesNotThrow(() -> jwtDecoder.decode(tokenValue));

        assertThat(decodedJwt.getSubject()).isEqualTo("test-user");
        assertThat(decodedJwt.getClaimAsString("iss")).isEqualTo("http://localhost:8080");
// Преобразуем URL в строку или сравниваем с java.net.URL
        assertThat(decodedJwt.getIssuer().toString()).isEqualTo("http://localhost:8080");
    }
}

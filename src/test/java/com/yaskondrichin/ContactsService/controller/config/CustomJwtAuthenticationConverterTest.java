package com.yaskondrichin.ContactsService.controller.config;

import com.yaskondrichin.ContactsService.config.CustomJwtAuthenticationConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CustomJwtAuthenticationConverterTest {

    private CustomJwtAuthenticationConverter converter;

    @BeforeEach
    void setUp() {
        converter = new CustomJwtAuthenticationConverter();
    }

    @Test
    @DisplayName("Должен успешно добавить префикс ROLE_, если в токене роль указана без него")
    void shouldAddRolePrefixWhenRoleDoesNotHaveIt() {
        // Given
        Jwt jwt = createMockJwt(Map.of("roles", "USER"));

        // When
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) converter.convert(jwt);

        // Then
        assertThat(authenticationToken).isNotNull();
        Collection<GrantedAuthority> authorities = authenticationToken.getAuthorities();

        assertThat(authorities)
                .hasSize(1)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_USER");
    }

    @Test
    @DisplayName("Не должен дублировать префикс, если роль в токене уже начинается с ROLE_")
    void shouldNotDuplicatePrefixWhenRoleAlreadyHasIt() {
        // Given
        Jwt jwt = createMockJwt(Map.of("roles", "ROLE_ADMIN"));

        // When
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) converter.convert(jwt);

        // Then
        assertThat(authenticationToken).isNotNull();
        Collection<GrantedAuthority> authorities = authenticationToken.getAuthorities();

        assertThat(authorities)
                .hasSize(1)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_ADMIN");
    }

    @Test
    @DisplayName("Должен вернуть пустой список прав, если claim 'roles' отсутствует в токене")
    void shouldReturnEmptyAuthoritiesWhenRolesClaimIsMissing() {
        // Given
        Jwt jwt = createMockJwt(Map.of("sub", "some-user-id")); // без claim "roles"

        // When
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) converter.convert(jwt);

        // Then
        assertThat(authenticationToken).isNotNull();
        assertThat(authenticationToken.getAuthorities()).isEmpty();
    }

    @Test
    @DisplayName("Должен вернуть пустой список прав, если claim 'roles' равен null")
    void shouldReturnEmptyAuthoritiesWhenRolesClaimIsNull() {
        // Given
        // В Map нельзя положить null для значения в стандартных реализациях,
        // поэтому создаем карту, где значение "roles" явно передается как null через изменяемый мап
        java.util.Map<String, Object> claims = new java.util.HashMap<>();
        claims.put("roles", null);
        Jwt jwt = createMockJwt(claims);

        // When
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) converter.convert(jwt);

        // Then
        assertThat(authenticationToken).isNotNull();
        assertThat(authenticationToken.getAuthorities()).isEmpty();
    }

    // Вспомогательный метод для создания объекта Jwt
    private Jwt createMockJwt(Map<String, Object> claims) {
        return new Jwt(
                "mock-token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"), // headers
                claims
        );
    }
}
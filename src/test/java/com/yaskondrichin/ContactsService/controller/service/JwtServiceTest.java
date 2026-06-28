package com.yaskondrichin.ContactsService.controller.service;

import com.yaskondrichin.ContactsService.DTO.TokenResponseDTO;
import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.model.Role;
import com.yaskondrichin.ContactsService.service.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @InjectMocks
    private JwtService jwtService;

    @Test
    void generateTokens_ShouldReturnAccessAndRefreshTokensWithCorrectClaims() {
        Login login = new Login();
        login.setId(123L);
        login.setLogin("daniil_dev");
        login.setRole(Role.USER);

        Jwt mockAccessJwt = mock(Jwt.class);
        when(mockAccessJwt.getTokenValue()).thenReturn("mocked-access-token-string");

        Jwt mockRefreshJwt = mock(Jwt.class);
        when(mockRefreshJwt.getTokenValue()).thenReturn("mocked-refresh-token-string");

        when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .thenReturn(mockAccessJwt)
                .thenReturn(mockRefreshJwt);

        TokenResponseDTO response = jwtService.generateTokens(login);

        assertNotNull(response);

        assertEquals("mocked-access-token-string", response.getAccessToken());
        assertEquals("mocked-refresh-token-string", response.getRefreshToken());

        ArgumentCaptor<JwtEncoderParameters> parametersCaptor = ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(jwtEncoder, times(2)).encode(parametersCaptor.capture());

        List<JwtEncoderParameters> allCapturedParameters = parametersCaptor.getAllValues();
        assertEquals(2, allCapturedParameters.size());

        JwtClaimsSet accessClaims = allCapturedParameters.get(0).getClaims();
        assertEquals("http://localhost:8080", accessClaims.getIssuer().toString()); // ИСПРАВЛЕНО
        assertEquals("123", accessClaims.getSubject());
        assertEquals(123L, (Long) accessClaims.getClaim("userId"));
        assertEquals("daniil_dev", accessClaims.getClaim("login"));
        assertEquals("USER", accessClaims.getClaim("roles"));
        assertNotNull(accessClaims.getIssuedAt());
        assertNotNull(accessClaims.getExpiresAt());

        JwtClaimsSet refreshClaims = allCapturedParameters.get(1).getClaims();
        assertEquals("http://localhost:8080", refreshClaims.getIssuer().toString()); // ИСПРАВЛЕНО
        assertEquals("123", refreshClaims.getSubject());

        assertNull(refreshClaims.getClaim("userId"));
        assertNull(refreshClaims.getClaim("login"));
        assertNull(refreshClaims.getClaim("roles"));
    }

    @Test
    void generateTokens_ShouldDefaultToUserRole_WhenRoleIsNull() {
        Login login = new Login();
        login.setId(999L);
        login.setLogin("guest_user");
        login.setRole(null);

        Jwt mockJwt = mock(Jwt.class);
        when(mockJwt.getTokenValue()).thenReturn("any-token");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);

        jwtService.generateTokens(login);

        ArgumentCaptor<JwtEncoderParameters> parametersCaptor = ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(jwtEncoder, times(2)).encode(parametersCaptor.capture());

        JwtClaimsSet accessClaims = parametersCaptor.getAllValues().get(0).getClaims();
        assertEquals("USER", accessClaims.getClaim("roles"), "Если роль равна null, должна подставиться роль по умолчанию 'USER'");
    }
}
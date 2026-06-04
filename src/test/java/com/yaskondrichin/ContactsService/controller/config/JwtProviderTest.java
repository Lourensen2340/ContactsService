package com.yaskondrichin.ContactsService.controller.config;

import com.yaskondrichin.ContactsService.config.JwtProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtProviderTest {

    @Mock
    private JwtDecoder jwtDecoder;

    @InjectMocks
    private JwtProvider jwtProvider;

    @Transactional
    @Test
    public void getUserIdFromToken_ValidToken_ShouldReturnUUID() {
        String token = "valid-token";
        UUID expectedUuid = UUID.randomUUID();

        Jwt mockJwt = Mockito.mock(Jwt.class);
        Mockito.when(mockJwt.getSubject()).thenReturn(expectedUuid.toString());
        Mockito.when(jwtDecoder.decode(token)).thenReturn(mockJwt);

        UUID actualUuid = jwtProvider.getUserIdFromToken(token, true);

        assertNotNull(actualUuid);
        assertEquals(expectedUuid, actualUuid);
    }

    @Transactional
    @Test
    public void getUserIdFromToken_InvalidToken_ShouldReturnNull() {
        String token = "invalid-token";
        Mockito.when(jwtDecoder.decode(token)).thenThrow(new RuntimeException("Invalid token"));

        UUID actualUuid = jwtProvider.getUserIdFromToken(token, true);

        assertNull(actualUuid);
    }
}

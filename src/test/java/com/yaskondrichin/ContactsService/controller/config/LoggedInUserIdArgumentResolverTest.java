package com.yaskondrichin.ContactsService.controller.config;

import com.yaskondrichin.ContactsService.config.LoggedInUserId;
import com.yaskondrichin.ContactsService.config.LoggedInUserIdArgumentResolver;
import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.repo.LoginRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class LoggedInUserIdArgumentResolverTest {

    @Mock
    private LoginRepository loginRepository;

    @InjectMocks
    private LoggedInUserIdArgumentResolver resolver;

    @AfterEach
    public void cleanUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void supportsParameter_ValidLongAnnotation_ShouldReturnTrue() {
        MethodParameter parameter = Mockito.mock(MethodParameter.class);
        Mockito.when(parameter.hasParameterAnnotation(LoggedInUserId.class)).thenReturn(true);
        Mockito.doReturn(Long.class).when(parameter).getParameterType();

        boolean result = resolver.supportsParameter(parameter);

        assertTrue(result);
    }

    @Test
    public void resolveArgument_WithUserIdClaim_ShouldReturnLongValue() throws Exception {
        Jwt jwt = Mockito.mock(Jwt.class);
        Mockito.when(jwt.hasClaim("userId")).thenReturn(true);
        Mockito.when(jwt.getClaim("userId")).thenReturn(42L);

        JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Object result = resolver.resolveArgument(null, null, null, null);

        assertEquals(42L, result);
    }

    @Test
    public void resolveArgument_WithoutUserIdClaim_ShouldFallbackToRepository() throws Exception {
        Jwt jwt = Mockito.mock(Jwt.class);
        Mockito.when(jwt.hasClaim("userId")).thenReturn(false);
        Mockito.when(jwt.getSubject()).thenReturn("testuser");

        Login mockLogin = new Login();
        mockLogin.setId(100L);

        Mockito.when(loginRepository.findByLogin("testuser")).thenReturn(Optional.of(mockLogin));

        JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Object result = resolver.resolveArgument(null, null, null, null);

        assertEquals(100L, result);
    }
}
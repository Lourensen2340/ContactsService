package com.yaskondrichin.ContactsService.controller.config;

import com.yaskondrichin.ContactsService.config.LoggedInUserId;
import com.yaskondrichin.ContactsService.config.LoggedInUserIdArgumentResolver;
import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.repo.LoginRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoggedInUserIdArgumentResolverTest {

    @Mock
    private LoginRepository loginRepository;

    @InjectMocks
    private LoggedInUserIdArgumentResolver resolver;

    @Mock
    private SecurityContext securityContext;

    @BeforeEach
    public void setUp() {
        // Устанавливаем мок контекста безопасности перед каждым тестом
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    public void cleanUp() {
        SecurityContextHolder.clearContext();
    }

    // --- Тесты для метода supportsParameter ---

    @Test
    public void supportsParameter_ValidLongAnnotation_ShouldReturnTrue() {
        MethodParameter parameter = mock(MethodParameter.class);
        when(parameter.hasParameterAnnotation(LoggedInUserId.class)).thenReturn(true);
        doReturn(Long.class).when(parameter).getParameterType();

        boolean result = resolver.supportsParameter(parameter);

        assertTrue(result);
    }

    @Test
    public void supportsParameter_MissingAnnotation_ShouldReturnFalse() {
        MethodParameter parameter = mock(MethodParameter.class);
        when(parameter.hasParameterAnnotation(LoggedInUserId.class)).thenReturn(false);

        boolean result = resolver.supportsParameter(parameter);

        assertFalse(result);
    }

    @Test
    public void supportsParameter_InvalidType_ShouldReturnFalse() {
        MethodParameter parameter = mock(MethodParameter.class);
        when(parameter.hasParameterAnnotation(LoggedInUserId.class)).thenReturn(true);
        doReturn(String.class).when(parameter).getParameterType();

        boolean result = resolver.supportsParameter(parameter);

        assertFalse(result);
    }

    // --- Тесты для метода resolveArgument ---

    @Test
    public void resolveArgument_NotJwtAuthentication_ShouldReturnNull() throws Exception {
        Authentication wrongAuth = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(wrongAuth);

        Object result = resolver.resolveArgument(null, null, null, null);

        assertNull(result);
    }

    @Test
    public void resolveArgument_WithUserIdClaim_ShouldReturnLongValue() throws Exception {
        JwtAuthenticationToken authentication = mock(JwtAuthenticationToken.class);
        Jwt jwt = mock(Jwt.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getToken()).thenReturn(jwt);
        when(jwt.hasClaim("userId")).thenReturn(true);
        when(jwt.getClaim("userId")).thenReturn(42L);

        Object result = resolver.resolveArgument(null, null, null, null);

        assertEquals(42L, result);
    }

    @Test
    public void resolveArgument_WithoutUserIdClaim_UserExistsInRepo_ShouldReturnIdFromRepo() throws Exception {
        JwtAuthenticationToken authentication = mock(JwtAuthenticationToken.class);
        Jwt jwt = mock(Jwt.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getToken()).thenReturn(jwt);
        when(jwt.hasClaim("userId")).thenReturn(false);
        when(jwt.getSubject()).thenReturn("testuser");

        Login mockLogin = new Login();
        mockLogin.setId(100L);
        when(loginRepository.findByLogin("testuser")).thenReturn(Optional.of(mockLogin));

        Object result = resolver.resolveArgument(null, null, null, null);

        assertEquals(100L, result);
    }

    @Test
    public void resolveArgument_WithoutUserIdClaim_UserDoesNotExistInRepo_ShouldReturnNull() throws Exception {
        JwtAuthenticationToken authentication = mock(JwtAuthenticationToken.class);
        Jwt jwt = mock(Jwt.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getToken()).thenReturn(jwt);
        when(jwt.hasClaim("userId")).thenReturn(false);
        when(jwt.getSubject()).thenReturn("unknownuser");
        when(loginRepository.findByLogin("unknownuser")).thenReturn(Optional.empty());

        Object result = resolver.resolveArgument(null, null, null, null);

        assertNull(result);
    }

    @Test
    public void resolveArgument_WithoutUserIdClaim_SubjectIsNull_FallbackToTokenName_ShouldReturnId() throws Exception {
        JwtAuthenticationToken authentication = mock(JwtAuthenticationToken.class);
        Jwt jwt = mock(Jwt.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getToken()).thenReturn(jwt);
        when(jwt.hasClaim("userId")).thenReturn(false);
        when(jwt.getSubject()).thenReturn(null);
        when(authentication.getName()).thenReturn("fallbackuser");

        Login mockLogin = new Login();
        mockLogin.setId(200L);
        when(loginRepository.findByLogin("fallbackuser")).thenReturn(Optional.of(mockLogin));

        Object result = resolver.resolveArgument(null, null, null, null);

        assertEquals(200L, result);
    }
}
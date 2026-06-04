package com.yaskondrichin.ContactsService.controller.config;

import com.yaskondrichin.ContactsService.config.AuthenticatedUserIdResolver;
import com.yaskondrichin.ContactsService.config.JwtProvider;
import com.yaskondrichin.ContactsService.config.LoggedInUserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticatedUserIdResolverTest {

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthenticatedUserIdResolver resolver;
    @Transactional
    @Test
    public void supportsParameter_ValidAnnotationAndType_ShouldReturnTrue() {
        MethodParameter parameter = Mockito.mock(MethodParameter.class);
        Mockito.when(parameter.getParameterAnnotation(LoggedInUserId.class)).thenReturn(Mockito.mock(LoggedInUserId.class));
        Mockito.doReturn(UUID.class).when(parameter).getParameterType();

        boolean result = resolver.supportsParameter(parameter);

        assertTrue(result);
    }
    @Transactional
    @Test
    public void resolveArgument_ValidBearerHeader_ShouldReturnUUID() {
        NativeWebRequest webRequest = Mockito.mock(NativeWebRequest.class);
        UUID expectedUuid = UUID.randomUUID();

        Mockito.when(webRequest.getHeader("Authorization")).thenReturn("Bearer super-token");
        Mockito.when(jwtProvider.getUserIdFromToken("super-token", true)).thenReturn(expectedUuid);

        Object result = resolver.resolveArgument(null, null, webRequest, null);

        assertEquals(expectedUuid, result);
    }
    @Transactional
    @Test
    public void resolveArgument_NoAuthorizationHeader_ShouldReturnNull() {
        NativeWebRequest webRequest = Mockito.mock(NativeWebRequest.class);
        Mockito.when(webRequest.getHeader("Authorization")).thenReturn(null);

        Object result = resolver.resolveArgument(null, null, webRequest, null);

        assertNull(result);
    }
}

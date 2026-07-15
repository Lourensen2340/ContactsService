package com.yaskondrichin.ContactsService.controller.config;



import com.yaskondrichin.ContactsService.config.argument_resolver.AuthenticadetUserId;
import com.yaskondrichin.ContactsService.config.argument_resolver.impl.AuthenticatedUserIdResolver;
import com.yaskondrichin.ContactsService.service.impl.JwtServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // КРИТИЧЕСКИ ВАЖНО: включает поддержку аннотаций Mockito
class AuthenticatedUserIdResolverTest {

    @Mock
    private JwtServiceImpl jwtService; // Создает мок для сервиса токенов

    @Mock
    private MethodParameter methodParameter; // Создает мок для параметра метода

    @Mock
    private AuthenticadetUserId authenticadetUserIdMock; // Создает мок для аннотации

    @Mock
    private NativeWebRequest webRequest; // Создает мок для HTTP-запроса

    @InjectMocks
    private AuthenticatedUserIdResolver resolver; // Автоматически внедряет jwtService внутрь resolver

    @Test
    void supportsParameter_ValidAnnotationAndType_ShouldReturnTrue() {
        // Настройка мока параметра метода
        when(methodParameter.getParameterAnnotation(AuthenticadetUserId.class))
                .thenReturn(authenticadetUserIdMock);
        when(methodParameter.getParameterType())
                .thenReturn((Class) UUID.class);

        // Вызов и проверка
        boolean result = resolver.supportsParameter(methodParameter);
        assertTrue(result);
    }

    @Test
    void resolveArgument_ValidBearerHeader_ShouldReturnUUID() {
        // 1. Данные для теста
        String rawToken = "my-valid-jwt-token";
        String authHeaderValue = "Bearer " + rawToken;
        UUID expectedUserId = UUID.randomUUID();

        // 2. Настройка заглушек (Stubbing)
        // Имитируем получение заголовка Authorization
        when(webRequest.getHeader("Authorization")).thenReturn(authHeaderValue);

        // Имитируем извлечение UUID из токена (jwtService теперь ТОЧНО не null)
        when(jwtService.getUserIdFromToken(rawToken, true)).thenReturn(expectedUserId);

        // 3. Вызов тестируемого метода
        Object result = resolver.resolveArgument(
                methodParameter,
                null,
                webRequest,
                null
        );

        // 4. Проверка результата
        assertEquals(expectedUserId, result);
    }
}

package com.yaskondrichin.ContactsService.config.argument_resolver.impl;


import com.yaskondrichin.ContactsService.config.argument_resolver.AuthenticadetUserId;
import com.yaskondrichin.ContactsService.config.argument_resolver.LoggedInUserId;
import com.yaskondrichin.ContactsService.service.impl.JwtServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import java.util.UUID;

@Component
@RequiredArgsConstructor // Автоматически инициализирует поле jwtProvider через конструктор
public class AuthenticatedUserIdResolver implements HandlerMethodArgumentResolver {

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    private final JwtServiceImpl jwtService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(AuthenticadetUserId.class) != null
                && parameter.getParameterType().equals(UUID.class);
    }

    @Override
    public Object resolveArgument(
            @NonNull MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            @NonNull NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        String authHeader = webRequest.getHeader(AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith(BEARER)) {
            String token = authHeader.substring(7);
            // Обратите внимание: true передается как обычное значение аргумента, без подсказок среды (isAccessToken:)
            return jwtService.getUserIdFromToken(token, true);
        }

        return null;
    }
}
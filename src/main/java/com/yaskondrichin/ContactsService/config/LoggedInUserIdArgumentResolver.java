package com.yaskondrichin.ContactsService.config;



import com.yaskondrichin.ContactsService.domain.repo.LoginRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j // Для логирования
@SecurityRequirement(name = LoggedInUserIdArgumentResolver.SECURITY_REQUIREMENT)
public class LoggedInUserIdArgumentResolver implements HandlerMethodArgumentResolver {

    public static final String SECURITY_REQUIREMENT = "Bearer Authentication";

    private final LoginRepository loginRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoggedInUserId.class)
                && parameter.getParameterType().equals(UUID.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            Jwt jwt = jwtAuthenticationToken.getToken();

            log.info("JWT Claims: {}", jwt.getClaims());

            // Исправление ошибки 1: безопасное извлечение Long из getClaim()
            if (jwt.hasClaim("userId")) {
                Object userIdClaim = jwt.getClaim("userId");
                if (userIdClaim instanceof Number number) {
                    return number.longValue();
                }
            }

            // Исправление ошибки 2: делаем переменную неизменяемой для лямбды
            final String username = jwt.getSubject() != null ? jwt.getSubject() : jwtAuthenticationToken.getName();

            log.info("Попытка найти пользователя по логину: {}", username);

            return loginRepository.findByLogin(username)
                    .map(loginEntity -> {
                        log.info("Пользователь найден! Локальный ID: {}", loginEntity.getId());
                        return loginEntity.getId();
                    })
                    .orElseGet(() -> {
                        log.error("Пользователь с логином '{}' не найден в таблице logins!", username);
                        return null;
                    });
        }

        log.error("Аутентификация не является экземпляром JwtAuthenticationToken!");
        return null;
    }
}
package com.yaskondrichin.ContactsService.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.util.UUID; // Оставляем ТОЛЬКО этот импорт! Удалили java.rmi.server.UID

@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final JwtDecoder jwtDecoder;

    // Исправлено: метод теперь возвращает UUID, а не UID
    public UUID getUserIdFromToken(String token, boolean isAccessToken){
        try {
            Jwt jwt = jwtDecoder.decode(token);
            String userSubject = jwt.getSubject();

            // Парсим строку в правильный тип java.util.UUID
            return UUID.fromString(userSubject);
        } catch (Exception e) {
            return null;
        }
    }
}
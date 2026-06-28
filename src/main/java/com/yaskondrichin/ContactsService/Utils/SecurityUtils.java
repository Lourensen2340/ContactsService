package com.yaskondrichin.ContactsService.Utils;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    /**
     * Извлекает userId из JWT токена и конвертирует его в Long.
     */
    public Long getUserIdFromJwt(Jwt jwt) {
        if (jwt == null) {
            throw new AccessDeniedException("Токен авторизации отсутствует");
        }

        // 1. Пробуем достать id из кастомного клейма "userId"
        String userIdStr = jwt.getClaimAsString("userId");

        // 2. Если там пусто, берем из стандартного "sub" (Subject токена)
        if (userIdStr == null) {
            userIdStr = jwt.getSubject();
        }

        // 3. Если и там пусто, выбрасываем исключение
        if (userIdStr == null) {
            throw new AccessDeniedException("Не удалось определить ID пользователя из токена");
        }

        try {
            return Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            throw new AccessDeniedException("ID пользователя в токене имеет неверный формат числа");
        }
    }
}

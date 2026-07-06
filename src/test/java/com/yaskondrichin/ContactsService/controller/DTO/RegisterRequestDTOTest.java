package com.yaskondrichin.ContactsService.controller.DTO;

import com.yaskondrichin.ContactsService.DTO.RegisterRequestDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestDTOTest {

    @Test
    void testNoArgsConstructorAndSettersAndGetters() {
        // Проверка конструктора без аргументов и сеттеров/геттеров
        RegisterRequestDTO dto = new RegisterRequestDTO();

        dto.setUsername("daniil_dev");
        dto.setPassword("securePass123");
        dto.setEmail("daniil@example.com");
        dto.setPhone("+375291112233");

        assertEquals("daniil_dev", dto.getUsername());
        assertEquals("securePass123", dto.getPassword());
        assertEquals("daniil@example.com", dto.getEmail());
        assertEquals("+375291112233", dto.getPhone());
    }

    @Test
    void testAllArgsConstructor() {
        // Проверка конструктора со всеми аргументами
        RegisterRequestDTO dto = new RegisterRequestDTO(
                "daniil_dev",
                "securePass123",
                "daniil@example.com",
                "+375291112233"
        );

        assertEquals("daniil_dev", dto.getUsername());
        assertEquals("securePass123", dto.getPassword());
        assertEquals("daniil@example.com", dto.getEmail());
        assertEquals("+375291112233", dto.getPhone());
    }

    @Test
    void testEqualsAndHashCode() {
        // Проверка корректности работы equals и hashCode для одинаковых объектов
        RegisterRequestDTO dto1 = new RegisterRequestDTO("user", "pass", "user@mail.com", "+375291111111");
        RegisterRequestDTO dto2 = new RegisterRequestDTO("user", "pass", "user@mail.com", "+375291111111");
        RegisterRequestDTO dto3 = new RegisterRequestDTO("other", "pass", "user@mail.com", "+375291111111");

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    void testToString() {
        // Проверка генерации строки toString
        RegisterRequestDTO dto = new RegisterRequestDTO("user", "pass", "user@mail.com", "+375291111111");
        String toStringResult = dto.toString();

        assertTrue(toStringResult.contains("username=user"));
        assertTrue(toStringResult.contains("email=user@mail.com"));
        assertTrue(toStringResult.contains("phone=+375291111111"));
    }
}

package com.yaskondrichin.ContactsService.controller.Mapper;

import com.yaskondrichin.ContactsService.DTO.LoginResponseDTO;
import com.yaskondrichin.ContactsService.Mapper.LoginMapper;
import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.model.Role;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class LoginMapperTest {

    private final LoginMapper mapper = Mappers.getMapper(LoginMapper.class);

    @Test
    void toResponseDto_ShouldMapFieldsCorrectly() {
        // 1. Подготовка данных
        Login login = new Login();
        login.setId(1L);
        login.setLogin("testLogin");
        login.setPass("secret123");
        login.setEmail("test@example.com");
        login.setPhone("+375291112233");
        login.setRole(Role.USER);

        // 2. Выполнение маппинга
        LoginResponseDTO dto = mapper.toResponseDto(login);

        // 3. Проверка (Assert)
        assertNotNull(dto);
        assertEquals(login.getLogin(), dto.getLogin());
        assertEquals(login.getEmail(), dto.getEmail());
        assertEquals(login.getPhone(), dto.getPhone());
        assertEquals(login.getRole(), dto.getRole());

        // Поле id тоже можно проверить, если оно есть в DTO
        assertEquals(login.getId(), dto.getId());
    }
}

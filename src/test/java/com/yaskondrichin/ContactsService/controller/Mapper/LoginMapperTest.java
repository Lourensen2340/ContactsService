package com.yaskondrichin.ContactsService.controller.Mapper;

import com.yaskondrichin.ContactsService.DTO.LoginResponseDTO;
import com.yaskondrichin.ContactsService.Mapper.LoginMapper;
import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.enums.Role;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LoginMapperTest {

    private final LoginMapper mapper = Mappers.getMapper(LoginMapper.class);

    @Test
    void toResponseDto_ShouldMapFieldsCorrectly() {
        Login login = new Login();
        login.setId(UUID.randomUUID()); // Исправлено: 1L -> UUID
        login.setLogin("testLogin");
        login.setPass("secret123");
        login.setEmail("test@example.com");
        login.setPhone("+375291112233");
        login.setRole(Role.USER);

        LoginResponseDTO dto = mapper.toResponseDto(login);

        assertNotNull(dto);
        assertEquals(login.getLogin(), dto.getLogin());
        assertEquals(login.getEmail(), dto.getEmail());
        assertEquals(login.getPhone(), dto.getPhone());
        assertEquals(login.getRole(), dto.getRole());
        assertEquals(login.getId(), dto.getId());
    }
}

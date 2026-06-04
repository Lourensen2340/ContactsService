package com.yaskondrichin.ContactsService.controller.Mapper;

import com.yaskondrichin.ContactsService.DTO.UserResponseDTO;
import com.yaskondrichin.ContactsService.Mapper.UserMapper;
import com.yaskondrichin.ContactsService.domain.model.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void toResponseDto_ShouldMapFieldsCorrectly() {
        // 1. Подготовка данных
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("securePassword123");
        user.setEmail("test@example.com");

        // 2. Выполнение маппинга
        UserResponseDTO dto = userMapper.toResponseDto(user);

        // 3. Проверка
        assertNotNull(dto);
        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getUsername(), dto.getUsername());
        assertEquals(user.getEmail(), dto.getEmail());


    }
}

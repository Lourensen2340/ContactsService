package com.yaskondrichin.ContactsService.controller.DTO;

import com.yaskondrichin.ContactsService.DTO.LoginResponseDTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LoginResponseDTOTest {

    @Test
    public void testGettersAndSetters() {
        LoginResponseDTO dto = new LoginResponseDTO();
        dto.setId(5L);
        dto.setLogin("daniil_dev");
        dto.setEmail("test@test.com"); // Метод Lombok для поля Email

        assertEquals(5L, dto.getId());
        assertEquals("daniil_dev", dto.getLogin());
        assertEquals("test@test.com", dto.getEmail());
    }
}

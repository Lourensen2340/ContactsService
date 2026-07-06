package com.yaskondrichin.ContactsService.controller.DTO;

import com.yaskondrichin.ContactsService.DTO.LoginResponseDTO;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class LoginResponseDTOTest {

    @Test
    public void testGettersAndSetters() {
        LoginResponseDTO dto = new LoginResponseDTO();
        UUID mockId = UUID.randomUUID();
        dto.setId(mockId);
        dto.setLogin("daniil_dev");
        dto.setEmail("test@test.com");

        assertEquals(mockId, dto.getId());
        assertEquals("daniil_dev", dto.getLogin());
        assertEquals("test@test.com", dto.getEmail());
    }
}

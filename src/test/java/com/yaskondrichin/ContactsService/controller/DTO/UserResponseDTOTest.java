package com.yaskondrichin.ContactsService.controller.DTO;

import com.yaskondrichin.ContactsService.DTO.UserResponseDTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserResponseDTOTest {

    @Test
    public void testGettersAndSetters() {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(10L);
        dto.setUsername("admin");
        dto.setEmail("admin@contacts.com");

        assertEquals(10L, dto.getId());
        assertEquals("admin", dto.getUsername());
        assertEquals("admin@contacts.com", dto.getEmail());
    }
}

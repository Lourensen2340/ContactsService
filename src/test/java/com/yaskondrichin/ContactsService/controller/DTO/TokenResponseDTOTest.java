package com.yaskondrichin.ContactsService.controller.DTO;

import com.yaskondrichin.ContactsService.DTO.TokenResponseDTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TokenResponseDTOTest {

    @Test
    public void testConstructorAndGetters() {
        TokenResponseDTO dto = new TokenResponseDTO("access_token_string", "refresh_token_string");

        assertEquals("access_token_string", dto.getAccessToken());
        assertEquals("refresh_token_string", dto.getRefreshToken());

        dto.setAccessToken("new_access");
        dto.setRefreshToken("new_refresh");

        assertEquals("new_access", dto.getAccessToken());
        assertEquals("new_refresh", dto.getRefreshToken());
    }
}

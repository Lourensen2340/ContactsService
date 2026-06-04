package com.yaskondrichin.ContactsService.controller.DTO;

import com.yaskondrichin.ContactsService.DTO.AuthResponseDTO;
import com.yaskondrichin.ContactsService.DTO.LoginResponseDTO;
import com.yaskondrichin.ContactsService.DTO.TokenResponseDTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AuthResponseDTOTest {

    @Test
    public void testGettersSettersAndBuilder() {
        LoginResponseDTO user = new LoginResponseDTO();
        TokenResponseDTO tokens = new TokenResponseDTO("access", "refresh");

        // Тест Билдера
        AuthResponseDTO dto = AuthResponseDTO.builder()
                .user(user)
                .tokens(tokens)
                .build();

        assertEquals(user, dto.getUser());
        assertEquals(tokens, dto.getTokens());

        // Тест Setter/Getter
        AuthResponseDTO emptyDto = new AuthResponseDTO();
        emptyDto.setUser(user);
        emptyDto.setTokens(tokens);

        assertEquals(user, emptyDto.getUser());
        assertEquals(tokens, emptyDto.getTokens());
    }
}

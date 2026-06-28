package com.yaskondrichin.ContactsService.controller;

import com.yaskondrichin.ContactsService.DTO.ContactDTO;
import com.yaskondrichin.ContactsService.Utils.SecurityUtils;
import com.yaskondrichin.ContactsService.config.JwtProvider;
import com.yaskondrichin.ContactsService.controller.config.TestSecurityConfig;
import com.yaskondrichin.ContactsService.domain.controller.ContactController;
import com.yaskondrichin.ContactsService.service.ContactService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContactController.class)
@Import({JwtProvider.class, TestSecurityConfig.class})
public class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContactService contactService;

    @MockitoBean
    private SecurityUtils securityUtils;

    @MockitoBean
    private JwtDecoder jwtDecoder;
    @MockitoBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @MockitoBean
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Test
    public void getAllContacts_ShouldReturnList() throws Exception {
        when(securityUtils.getUserIdFromJwt(any(Jwt.class))).thenReturn(1L);
        // ИСПРАВЛЕНИЕ: Вызываем оригинальный метод findAllByUserId вместо getAllContacts
        when(contactService.findAllByUserId(1L)).thenReturn(List.of(new ContactDTO()));

        mockMvc.perform(get("/api/v1/contacts")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk());
    }

    @Test
    public void createContact_ShouldReturnOk() throws Exception {
        ContactDTO outputDto = new ContactDTO();
        when(securityUtils.getUserIdFromJwt(any(Jwt.class))).thenReturn(1L);
        // ИСПРАВЛЕНИЕ: Метод называется create и теперь корректно возвращает DTO
        when(contactService.create(any(ContactDTO.class), eq(1L))).thenReturn(outputDto);

        String contactJson = "{\"name\": \"Ivan\", \"surname\": \"Ivanov\", \"phone\": \"+375291112233\"}";

        mockMvc.perform(post("/api/v1/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contactJson)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    public void updateContact_ShouldReturnOk_WhenValidData() throws Exception {
        ContactDTO outputDto = new ContactDTO();
        when(securityUtils.getUserIdFromJwt(any(Jwt.class))).thenReturn(1L);
        // ИСПРАВЛЕНИЕ: Метод называется update и теперь корректно возвращает DTO
        when(contactService.update(eq(15L), any(ContactDTO.class), eq(1L))).thenReturn(outputDto);

        String contactJson = "{\"name\": \"Ivan Updated\", \"surname\": \"Ivanov\", \"phone\": \"+375291112233\"}";

        mockMvc.perform(put("/api/v1/contacts/15")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contactJson)
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                                .jwt(jwtBuilder -> jwtBuilder
                                        .claim("userId", 1L)
                                        .claim("sub", "user")))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void deleteContact_ShouldReturnOk() throws Exception {
        when(securityUtils.getUserIdFromJwt(any(Jwt.class))).thenReturn(1L);
        // Метод deleteContact совпадает с оригинальной сигнатурой (void)
        doNothing().when(contactService).deleteContact(15L, 1L);

        mockMvc.perform(delete("/api/v1/contacts/15")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                                .jwt(jwtBuilder -> jwtBuilder.claim("userId", 1L)))
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}
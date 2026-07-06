package com.yaskondrichin.ContactsService.controller;

import com.yaskondrichin.ContactsService.DTO.ContactDTO;
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
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

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
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @MockitoBean
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    private final UUID mockUserId = UUID.randomUUID();
    private final UUID mockContactId = UUID.randomUUID();

    @Test
    public void getAllContacts_ShouldReturnList() throws Exception {
        when(contactService.findAllByUserId(mockUserId)).thenReturn(List.of(new ContactDTO()));

        mockMvc.perform(get("/api/contacts")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                                .jwt(jwt -> jwt.subject(mockUserId.toString()))))
                .andExpect(status().isOk());
    }

    @Test
    public void createContact_ShouldReturnOk() throws Exception {
        ContactDTO outputDto = new ContactDTO();
        when(contactService.create(any(ContactDTO.class), eq(mockUserId))).thenReturn(outputDto);

        String contactJson = "{\"name\": \"Ivan\", \"surname\": \"Ivanov\", \"phone\": \"+375291112233\"}";

        mockMvc.perform(post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contactJson)
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                                .jwt(jwt -> jwt.subject(mockUserId.toString())))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    public void updateContact_ShouldReturnOk_WhenValidData() throws Exception {
        ContactDTO outputDto = new ContactDTO();
        when(contactService.update(eq(mockContactId), any(ContactDTO.class), eq(mockUserId))).thenReturn(outputDto);

        String contactJson = "{\"name\": \"Ivan Updated\", \"surname\": \"Ivanov\", \"phone\": \"+375291112233\"}";

        mockMvc.perform(put("/api/contacts/" + mockContactId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contactJson)
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                                .jwt(jwt -> jwt.subject(mockUserId.toString())))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void deleteContact_ShouldReturnNoContent() throws Exception {
        doNothing().when(contactService).deleteContact(mockContactId, mockUserId);

        mockMvc.perform(delete("/api/contacts/" + mockContactId)
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                                .jwt(jwt -> jwt.subject(mockUserId.toString())))
                        .with(csrf()))
                .andExpect(status().isNoContent()); // Исправлено на 204 No Content
    }
}
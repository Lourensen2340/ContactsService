package com.yaskondrichin.ContactsService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaskondrichin.ContactsService.DTO.AssignRoleDTO;
import com.yaskondrichin.ContactsService.config.AuthenticatedUserIdResolver;
import com.yaskondrichin.ContactsService.config.JwtProvider;
import com.yaskondrichin.ContactsService.config.LoggedInUserIdArgumentResolver;
import com.yaskondrichin.ContactsService.domain.controller.AdminController;
import com.yaskondrichin.ContactsService.domain.enums.Role;
import com.yaskondrichin.ContactsService.domain.repo.LoginRepository;
import com.yaskondrichin.ContactsService.service.LoginService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@Import(JwtProvider.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LoginService loginService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private LoginRepository loginRepository;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private LoggedInUserIdArgumentResolver loggedInUserIdArgumentResolver;

    @MockitoBean
    private AuthenticatedUserIdResolver authenticatedUserIdResolver;

    @Test
    public void assignRole_WhenUserIsAdmin_ShouldReturnOk() throws Exception {
        UUID mockUserId = UUID.randomUUID(); // Исправлено: long -> UUID
        AssignRoleDTO dto = new AssignRoleDTO();
        dto.setUserId(mockUserId);
        dto.setRole(Role.ROLE_ADMIN);

        Mockito.doNothing().when(loginService).assignRole(Mockito.any(AssignRoleDTO.class));

        mockMvc.perform(put("/api/v1/admin/assign-role")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Роль успешно изменена профилю с ID " + mockUserId));
    }

    @Test
    public void assignRole_WhenUserIsRegularUser_ShouldReturnForbidden() throws Exception {
        UUID mockUserId = UUID.randomUUID(); // Исправлено: long -> UUID
        UUID mockLoggedInId = UUID.randomUUID(); // Исправлено: long -> UUID

        AssignRoleDTO dto = new AssignRoleDTO();
        dto.setUserId(mockUserId);
        dto.setRole(Role.ROLE_ADMIN);

        Mockito.when(loggedInUserIdArgumentResolver.supportsParameter(Mockito.any())).thenReturn(true);
        Mockito.when(loggedInUserIdArgumentResolver.resolveArgument(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(mockLoggedInId);

        Mockito.when(authenticatedUserIdResolver.supportsParameter(Mockito.any())).thenReturn(true);
        Mockito.when(authenticatedUserIdResolver.resolveArgument(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(mockLoggedInId);

        mockMvc.perform(put("/api/v1/admin/assign-role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))) // Исправлена ручная строка на objectMapper
                .andExpect(status().isForbidden());
    }
}
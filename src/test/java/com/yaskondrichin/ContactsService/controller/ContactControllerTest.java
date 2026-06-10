package com.yaskondrichin.ContactsService.controller;

import com.yaskondrichin.ContactsService.controller.config.TestSecurityConfig;
import com.yaskondrichin.ContactsService.domain.controller.ContactController;
import com.yaskondrichin.ContactsService.service.ContactService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@WebMvcTest(ContactController.class)
@Import(TestSecurityConfig.class)
public class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContactService contactService;

    @MockitoBean
    private JwtDecoder jwtDecoder;


    @MockitoBean
    private com.yaskondrichin.ContactsService.config.JwtProvider jwtProvider;
    @MockitoBean
    private com.yaskondrichin.ContactsService.domain.repo.LoginRepository loginRepository;

    @Test
    public void getAll_WhenUser_ShouldReturnForbidden() throws Exception {
        // Если ваш контроллер защищен и роль не ADMIN, должен быть 403
        mockMvc.perform(get("/api/v1/contacts") // Скобка открыта
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))) // Все настройки внутри put
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": 15, \"role\": \"ROLE_ADMIN\"}")) // Закрывающая скобка ТОЛЬКО здесь
                .andDo(print())
                .andExpect(status().isForbidden());

    }
}
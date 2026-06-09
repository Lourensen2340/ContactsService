package com.yaskondrichin.ContactsService.controller;

import com.yaskondrichin.ContactsService.DTO.ContactDTO;
import com.yaskondrichin.ContactsService.controller.config.TestSecurityConfig;
import com.yaskondrichin.ContactsService.domain.controller.ContactController;
import com.yaskondrichin.ContactsService.service.ContactService;
<<<<<<< HEAD
import com.yaskondrichin.ContactsService.config.SecurityConfig; // <-- ИМПОРТИРУЙТЕ ВАШ ОСНОВНОЙ SecurityConfig

=======
>>>>>>> 55255faa9f12612568d8eb772844cdd9b017da7c
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
<<<<<<< HEAD
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan; // <-- ДОБАВИТЬ
import org.springframework.context.annotation.FilterType;    // <-- ДОБАВИТЬ
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
=======
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.annotation.DirtiesContext;
>>>>>>> 55255faa9f12612568d8eb772844cdd9b017da7c
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

//import static org.springframework.http.RequestEntity.put;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


<<<<<<< HEAD
@WebMvcTest(
        value = ContactController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = SecurityConfig.class // <-- Исключаем основной конфиг безопасности из этого теста
        )
)
@Import({
        TestSecurityConfig.class,
        AopAutoConfiguration.class, // Активирует АОП-прокси для работы @PreAuthorize
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
})
public class ContactControllerTest {

=======
@WebMvcTest(ContactController.class)
@Import(TestSecurityConfig.class)
public class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContactService contactService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

>>>>>>> 55255faa9f12612568d8eb772844cdd9b017da7c
    @MockitoBean
    private com.yaskondrichin.ContactsService.config.JwtProvider jwtProvider;
    @MockitoBean
    private com.yaskondrichin.ContactsService.domain.repo.LoginRepository loginRepository;
    @Test
    public void getAll_WhenAdmin_ShouldReturnListOfContacts() throws Exception {
        ContactDTO dto = new ContactDTO();
        dto.setName("Даниил");
        Mockito.when(contactService.findAllByUserId(Mockito.any())).thenReturn(List.of(dto));

<<<<<<< HEAD
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getAll_WhenUser_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/contacts")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                        .andExpect(status().isForbidden());
=======
        mockMvc.perform(get("/api/v1/contacts")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$[0].name").value("Даниил"));
    }

    @Test
    public void getAll_WhenUser_ShouldReturnForbidden() throws Exception {
        // Если ваш контроллер защищен и роль не ADMIN, должен быть 403
        mockMvc.perform(get("/api/v1/contacts") // Скобка открыта
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))) // Все настройки внутри put
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": 15, \"role\": \"ROLE_ADMIN\"}")) // Закрывающая скобка ТОЛЬКО здесь
                .andDo(print())
                .andExpect(status().isForbidden());
>>>>>>> 55255faa9f12612568d8eb772844cdd9b017da7c
    }
}
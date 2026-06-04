package com.yaskondrichin.ContactsService.controller.exeption;

import org.junit.jupiter.api.BeforeEach;
import com.yaskondrichin.ContactsService.exception.GlobalExceptionHandler;
import com.yaskondrichin.ContactsService.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    // Создаем фиктивный контроллер внутри теста, чтобы вызывать ошибки
    @RestController
    static class TestController {
        @GetMapping("/test-not-found")
        public void throwNotFound() {
            throw new ResourceNotFoundException("Ресурс не найден");
        }

        @GetMapping("/test-runtime")
        public void throwRuntime() {
            throw new RuntimeException("Что-то пошло не так");
        }
    }

    @BeforeEach
    void setUp() {
        // Настраиваем MockMvc, подключая наш GlobalExceptionHandler
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void handleResourceNotFound_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/test-not-found")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Ресурс не найден"));
    }

    @Test
    void handleRuntimeException_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/test-runtime")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Что-то пошло не так"));
    }
}

package com.yaskondrichin.ContactsService.controller.exeption;

import com.yaskondrichin.ContactsService.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.junit.jupiter.api.Assertions.*;

class ResourceNotFoundExceptionTest {

    @Test
    void shouldHaveNotFoundStatus() {
        // Проверяем, что на классе есть аннотация @ResponseStatus с кодом 404
        ResponseStatus annotation = ResourceNotFoundException.class.getAnnotation(ResponseStatus.class);
        assertNotNull(annotation, "Класс должен иметь аннотацию @ResponseStatus");
        assertEquals(HttpStatus.NOT_FOUND, annotation.value(), "Статус должен быть 404 NOT_FOUND");
    }

    @Test
    void shouldHoldCorrectMessage() {
        String message = "Контакт не найден";
        ResourceNotFoundException exception = new ResourceNotFoundException(message);

        assertEquals(message, exception.getMessage(), "Сообщение исключения должно совпадать с переданным");
    }
}

package com.yaskondrichin.ContactsService.controller.DTO;

import com.yaskondrichin.ContactsService.DTO.ContactDTO;
import com.yaskondrichin.ContactsService.Mapper.ContactMapper;
import com.yaskondrichin.ContactsService.domain.model.Contact;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // Загружает Spring-контекст для работы MapStruct
class ContactMapperTest {

    @Autowired
    private ContactMapper mapper;

    @Test
    void toDto_ShouldMapFieldsCorrectly() {
        Contact contact = new Contact();
        contact.setName("Даниил");
        contact.setSurname("Кондричин");
        contact.setPhone("+375291112233");
        contact.setEmail("daniil@example.com");

        // ИСПРАВЛЕНО: Вызываем toDTO() в точном соответствии с именем метода в интерфейсе
        ContactDTO dto = mapper.toDTO(contact);

        assertNotNull(dto);
        assertEquals("Даниил", dto.getName());
        assertEquals("Кондричин", dto.getSurname());
        assertEquals("+375291112233", dto.getPhone());
        assertEquals("daniil@example.com", dto.getEmail());
    }

    @Test
    void toEntity_ShouldIgnoreFields() {
        ContactDTO dto = new ContactDTO();
        dto.setId(42L); // Это поле ДОЛЖНО быть проигнорировано по конфигурации маппера
        dto.setName("Новое имя");
        dto.setEmail("test@mail.com"); // Это поле должно успешно перенестись

        Contact contact = mapper.toEntity(dto);

        assertNotNull(contact);
        assertEquals("Новое имя", contact.getName());
        assertEquals("test@mail.com", contact.getEmail());

        // ИСПРАВЛЕНО: Проверяем реальное игнорирование id, заданное в интерфейсе маппера
        assertNull(contact.getId(), "Поле id должно быть проигнорировано маппером и остаться null");
    }
}

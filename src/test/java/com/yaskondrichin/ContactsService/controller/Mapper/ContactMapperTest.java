package com.yaskondrichin.ContactsService.controller.Mapper;

import com.yaskondrichin.ContactsService.DTO.ContactDTO;
import com.yaskondrichin.ContactsService.Mapper.ContactMapper;
import com.yaskondrichin.ContactsService.domain.model.Contact;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
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

        ContactDTO dto = mapper.toDTO(contact);

        assertNotNull(dto);
        assertEquals("Даниил", dto.getName());
        assertEquals("Кондричин", dto.getSurname());
        assertEquals("+375291112233", dto.getPhone());
        assertEquals("daniil@example.com", dto.getEmail());
    }

    @Test
    void toEntity_ShouldMapAllFieldsIncludingId() {
        ContactDTO dto = new ContactDTO();
        UUID mockId = UUID.randomUUID();

        dto.setId(mockId);
        dto.setName("Новое имя");
        dto.setEmail("test@mail.com");

        Contact contact = mapper.toEntity(dto);

        assertNotNull(contact);
        assertEquals("Новое имя", contact.getName());
        assertEquals("test@mail.com", contact.getEmail());

        // Исправлено: проверяем, что ID успешно переносится, так как маппер НЕ игнорирует его
        assertEquals(mockId, contact.getId(), "Поле id должно быть успешно перенесено маппером из DTO в Entity");
    }
}
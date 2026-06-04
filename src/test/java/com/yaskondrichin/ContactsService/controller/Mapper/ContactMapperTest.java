package com.yaskondrichin.ContactsService.controller.Mapper;

import com.yaskondrichin.ContactsService.DTO.ContactDTO;
import com.yaskondrichin.ContactsService.Mapper.ContactMapper;
import com.yaskondrichin.ContactsService.domain.model.Contact;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import static org.junit.jupiter.api.Assertions.*;


@ComponentScan(basePackages = "com.yaskondrichin.ContactsService")
    @SpringBootTest // Загружает Spring-контекст
    class ContactMapperTest {

        @Autowired // Внедряем бин, созданный Spring
        private ContactMapper mapper;

        @Test
        void toDto_ShouldMapFieldsCorrectly() {
            Contact contact = new Contact();
            contact.setName("Даниил");

            ContactDTO dto = mapper.toDto(contact);

            assertNotNull(dto);
            assertEquals("Даниил", dto.getName());
        }

    @Test
    void toEntity_ShouldIgnoreFields() {
        ContactDTO dto = new ContactDTO();
        dto.setName("Новое имя");

        Contact contact = mapper.toEntity(dto);

        assertEquals("Новое имя", contact.getName());
        assertNull(contact.getEmail(), "Поле email должно быть проигнорировано маппером");
    }
}

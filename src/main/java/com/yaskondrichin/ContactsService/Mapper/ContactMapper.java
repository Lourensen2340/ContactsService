package com.yaskondrichin.ContactsService.Mapper;

import com.yaskondrichin.ContactsService.DTO.ContactDTO;
import com.yaskondrichin.ContactsService.domain.model.Contact;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ContactMapper {

    // Из списка сущностей в список DTO ответов (для findAll)
    List<ContactDTO> toDtoList(List<Contact> contacts);

    // Из сущности в полноценный DTO ответа (с ID)
    ContactDTO toDTO(Contact contact);

    // Маппинг из DTO создания в сущность. ID и Владельца игнорируем, их проставит БД и Сервис
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "users", ignore = true)
    Contact toEntity(ContactDTO contactDTO);

    // Метод для красивого обновления существующей сущности без ручных сеттеров
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "users", ignore = true)
    void updateEntityFromDto(ContactDTO source, @MappingTarget Contact target);

    // ТОТ САМЫЙ ДЕФОЛТНЫЙ МЕТОД, который решает проблему MapStruct с UUID
    default UUID map(UUID value) {
        return value;
    }


}
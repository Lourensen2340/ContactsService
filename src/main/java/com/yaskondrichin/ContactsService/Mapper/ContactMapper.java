package com.yaskondrichin.ContactsService.Mapper;

import com.yaskondrichin.ContactsService.DTO.ContactDTO;
import com.yaskondrichin.ContactsService.domain.model.Contact;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.List;

@org.mapstruct.Mapper(componentModel = "spring")
public interface ContactMapper {

    List<ContactDTO> toDtoList(List<Contact> contacts);

    ContactDTO toDto(Contact contact);
    @Mapping(target = "user", ignore = true)     // Говорим мапперу не трогать поле user
    @Mapping(target = "surname", ignore = true)  // Игнорируем поля, которых нет в базовом ContactDTO
    @Mapping(target = "email", ignore = true)
    Contact toEntity(ContactDTO contactDTO);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDetails(ContactDTO source, @MappingTarget Contact target);
}
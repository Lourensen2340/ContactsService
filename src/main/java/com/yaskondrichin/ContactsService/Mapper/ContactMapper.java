package com.yaskondrichin.ContactsService.Mapper;



import com.yaskondrichin.ContactsService.DTO.ContactDTO;
import com.yaskondrichin.ContactsService.domain.model.Contact;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ContactMapper {

    // Конвертация из сущности БД в безопасный DTO для клиента
    ContactDTO toDTO(Contact contact);

    // Конвертация из DTO в сущность БД
    // ИСПРАВЛЕНО: Убрали старый target = "users".
    // Указываем MapStruct игнорировать поле 'login', так как мы устанавливаем его вручную в сервисе через contact.setLogin(owner)
    @Mapping(target = "login", ignore = true)
    Contact toEntity(ContactDTO contactDTO);

    // Готовый метод для маппинга списков контактов
    List<ContactDTO> toDtoList(List<Contact> contacts);
}
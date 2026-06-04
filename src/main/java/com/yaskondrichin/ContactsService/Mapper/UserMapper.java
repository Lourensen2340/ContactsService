package com.yaskondrichin.ContactsService.Mapper;

import com.yaskondrichin.ContactsService.DTO.UserResponseDTO;
import com.yaskondrichin.ContactsService.domain.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Превращает сущность User (БД) в безопасный DTO для отправки клиенту
    UserResponseDTO toResponseDto(User user);
}
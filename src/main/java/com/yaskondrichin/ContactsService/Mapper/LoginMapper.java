package com.yaskondrichin.ContactsService.Mapper;

import com.yaskondrichin.ContactsService.DTO.LoginResponseDTO;
import com.yaskondrichin.ContactsService.domain.model.Login;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")

public interface LoginMapper {
    @Mapping(source = "login", target = "login")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "role", target = "role")
    LoginResponseDTO toResponseDto(Login login);
}
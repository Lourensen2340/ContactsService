package com.yaskondrichin.ContactsService.Mapper;

import com.yaskondrichin.ContactsService.DTO.TokenValidationDTO;
import com.yaskondrichin.ContactsService.domain.model.UserToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface TokenMapper {

    @Mapping(target = "valid", source = "token", qualifiedByName = "calculateIsValid")
    TokenValidationDTO toDto(UserToken token);

    @Named("calculateIsValid")
    default boolean calculateIsValid(UserToken token) {
        if (token == null) return false;
        return !token.isRevoked() && token.getExpiryDate().isAfter(Instant.now());
    }
}
package com.yaskondrichin.ContactsService.service;

import com.yaskondrichin.ContactsService.DTO.AuthResponseDTO;
import com.yaskondrichin.ContactsService.DTO.TokenResponseDTO;
import com.yaskondrichin.ContactsService.domain.model.Login;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface JwtService {
    TokenResponseDTO generateTokens(Login user);
    UUID getUserIdFromToken(String token, boolean isAccessToken);

    @Transactional(readOnly = true)
    AuthResponseDTO generateTokensByUserId(UUID userId);
}
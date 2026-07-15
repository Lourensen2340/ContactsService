package com.yaskondrichin.ContactsService.service.impl;

import com.yaskondrichin.ContactsService.DTO.TokenValidationDTO;
import com.yaskondrichin.ContactsService.Mapper.TokenMapper;
import com.yaskondrichin.ContactsService.domain.model.UserToken;
import com.yaskondrichin.ContactsService.domain.repo.UserTokenRepository;
import com.yaskondrichin.ContactsService.service.TokenValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenValidationServiceImpl implements TokenValidationService {

    private final UserTokenRepository tokenRepository;
    private final TokenMapper tokenMapper;

    @Override
    @Transactional
    public void saveToken(String tokenValue, UUID userId, Instant expiryDate) {
        UserToken token = UserToken.builder()
                .tokenValue(tokenValue)
                .userId(userId)
                .expiryDate(expiryDate)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    @Override
    @Transactional(readOnly = true)
    public TokenValidationDTO validateToken(String tokenValue) {
        return tokenRepository.findByTokenValue(tokenValue)
                .map(tokenMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Токен не найден в белом списке"));
    }

    @Override
    @Transactional
    public void revokeToken(String tokenValue) {
        UserToken token = tokenRepository.findByTokenValue(tokenValue)
                .orElseThrow(() -> new RuntimeException("Токен не найден"));
        token.setRevoked(true);
        tokenRepository.save(token);
    }

    @Override
    @Transactional
    public void revokeAllUserTokens(UUID userId) {
        tokenRepository.deleteByUserId(userId);
    }
}

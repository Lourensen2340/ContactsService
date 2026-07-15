package com.yaskondrichin.ContactsService.service.impl;

import com.yaskondrichin.ContactsService.DTO.AuthResponseDTO;
import com.yaskondrichin.ContactsService.DTO.TokenResponseDTO;

import com.yaskondrichin.ContactsService.Mapper.LoginMapper;
import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.repo.LoginRepository;
import com.yaskondrichin.ContactsService.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder; // Добавили декодер для валидации/чтения токенов
    private final LoginRepository loginRepository;

    private final LoginMapper loginMapper;


    @Override
    public TokenResponseDTO generateTokens(Login user) {
        Instant now = Instant.now();
        String userIdStr = user.getId().toString();

        String roleName = user.getRole() != null ? user.getRole().name() : "USER";
        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();

        JwtClaimsSet accessClaims = JwtClaimsSet.builder()
                .issuer("http://localhost:8080")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(userIdStr)
                .claim("userId", user.getId())
                .claim("login", user.getLogin())
                .claim("roles", roleName)
                .build();

        JwtClaimsSet refreshClaims = JwtClaimsSet.builder()
                .issuer("http://localhost:8080")
                .issuedAt(now)
                .expiresAt(now.plus(30, ChronoUnit.DAYS))
                .subject(userIdStr)
                .build();

        String access = jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, accessClaims)).getTokenValue();
        String refresh = jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, refreshClaims)).getTokenValue();

        return new TokenResponseDTO(access, refresh);
    }

    @Override
    public UUID getUserIdFromToken(String token, boolean isAccessToken) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            String userSubject = jwt.getSubject();

            // Парсим строку в правильный тип java.util.UUID
            return UUID.fromString(userSubject);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public AuthResponseDTO generateTokensByUserId(UUID userId) {
        Login user = loginRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with ID " + userId + " does not exist"));

        TokenResponseDTO tokens = this.generateTokens(user);

        return AuthResponseDTO.builder()
                .user(loginMapper.toResponseDto(user))
                .tokens(tokens)
                .build();
    }

}
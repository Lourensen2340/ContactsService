package com.yaskondrichin.ContactsService.service;

import com.yaskondrichin.ContactsService.DTO.TokenResponseDTO;
import com.yaskondrichin.ContactsService.domain.model.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtEncoder jwtEncoder;

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
}

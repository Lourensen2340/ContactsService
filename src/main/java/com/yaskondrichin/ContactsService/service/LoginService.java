package com.yaskondrichin.ContactsService.service;

import com.yaskondrichin.ContactsService.DTO.*;
import com.yaskondrichin.ContactsService.exception.ResourceNotFoundException;
import com.yaskondrichin.ContactsService.Mapper.LoginMapper;
import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.repo.LoginRepository;
import com.yaskondrichin.ContactsService.domain.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final LoginRepository loginRepository;
    private final LoginMapper loginMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    @Transactional
    public AuthResponseDTO register(LoginRequestDTO dto) {
        if (loginRepository.findByLogin(dto.getUsername()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        Login entity = new Login();
        entity.setLogin(dto.getUsername());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setPass(passwordEncoder.encode(dto.getPassword()));
        entity.setRole(Role.USER);

        Login saved = loginRepository.save(entity);
        TokenResponseDTO tokens = generateTokens(saved);

        return AuthResponseDTO.builder()
                .user(loginMapper.toResponseDto(saved))
                .tokens(tokens)
                .build();
    }

    private TokenResponseDTO generateTokens(Login user) {
        Instant now = Instant.now();
        String userIdStr = user.getId().toString();

        String roleName = user.getRole() != null ? user.getRole().name() : "USER";
        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();

        JwtClaimsSet accessClaims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(userIdStr)
                .claim("userId", user.getId())
                .claim("login", user.getLogin())
                .claim("roles", roleName)
                .build();

        JwtClaimsSet refreshClaims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(30, ChronoUnit.DAYS))
                .subject(userIdStr)
                .build();

        String access = jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, accessClaims)).getTokenValue();
        String refresh = jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, refreshClaims)).getTokenValue();

        return new TokenResponseDTO(access, refresh);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public LoginResponseDTO getLastCreatedUser() {
        return loginRepository.findFirstByOrderByIdDesc()
                .map(loginMapper::toResponseDto)
                .orElseThrow(() -> new RuntimeException("No users found"));
    }

    // ИСПРАВЛЕНО: ИзмененоhasRole('ADMIN') на isAuthenticated(), чтобы обычные пользователи могли вызывать этот метод
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public Login getMe(Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());
        return loginRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    // ИСПРАВЛЕНО: Упрощено выражение проверки владельца токена через более надежный authentication.name
    @PreAuthorize("hasRole('ADMIN') or #id.toString() == authentication.name")
    @Transactional(readOnly = true)
    public LoginResponseDTO findById(Long id) {
        Login login = loginRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь с ID " + id + " не найден"));
        return loginMapper.toResponseDto(login);
    }

    @Transactional(readOnly = true)
    public AuthResponseDTO generateTokensByUserId(Long userId) {
        Login user = loginRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with ID " + userId + " does not exist"));

        TokenResponseDTO tokens = generateTokens(user);

        return AuthResponseDTO.builder()
                .user(loginMapper.toResponseDto(user))
                .tokens(tokens)
                .build();
    }

    @Transactional
    public void assignRole(AssignRoleDTO assignRoleDTO) {
        Login login = loginRepository.findById(assignRoleDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с ID " + assignRoleDTO.getUserId() + " не найден"));

        login.setRole(assignRoleDTO.getRole());
        loginRepository.save(login);
    }
}
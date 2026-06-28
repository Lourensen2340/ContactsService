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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final LoginRepository loginRepository;
    private final LoginMapper loginMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponseDTO register(LoginRequestDTO dto) {
        if (loginRepository.findByLogin(dto.getLogin()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        Login saved = createLoginEntity(dto);
        TokenResponseDTO tokens = jwtService.generateTokens(saved);

        return AuthResponseDTO.builder()
                .user(loginMapper.toResponseDto(saved))
                .tokens(tokens)
                .build();
    }
    private Login createLoginEntity(LoginRequestDTO dto) {
        Login entity = new Login();
        entity.setLogin(dto.getLogin());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setPass(passwordEncoder.encode(dto.getPassword()));
        entity.setRole(Role.USER);

        Login saved = loginRepository.save(entity);

        return entity;
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

        TokenResponseDTO tokens = jwtService.generateTokens(user);

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

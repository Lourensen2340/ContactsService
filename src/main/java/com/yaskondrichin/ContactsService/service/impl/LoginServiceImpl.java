package com.yaskondrichin.ContactsService.service.impl;

import com.yaskondrichin.ContactsService.DTO.*;
import com.yaskondrichin.ContactsService.Mapper.LoginMapper;
import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.repo.LoginRepository;
import com.yaskondrichin.ContactsService.exception.ResourceNotFoundException;
import com.yaskondrichin.ContactsService.service.JwtService;
import com.yaskondrichin.ContactsService.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final LoginRepository loginRepository;
    private final LoginMapper loginMapper;
    private final JwtService jwtService; // Внедряем через интерфейс

    @Override
    public LoginResponseDTO getLastCreatedUser() {
        return loginRepository.findFirstByOrderByIdDesc()
                .map(loginMapper::toResponseDto)
                .orElseThrow(() -> new RuntimeException("No users found"));
    }

    @Override
    public Login getMe(Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return loginRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    @Override
    public LoginResponseDTO findById(UUID id) {
        Login login = loginRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь с ID " + id + " не найден"));
        return loginMapper.toResponseDto(login);
    }

    @Override
    public AuthResponseDTO generateTokensByUserId(UUID userId) {
        // Просто перенаправляем запрос в jwtService, где логика уже написана
        return jwtService.generateTokensByUserId(userId);
    }

    @Override
    public void assignRole(AssignRoleDTO assignRoleDTO) {
        Login login = loginRepository.findById(assignRoleDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с ID " + assignRoleDTO.getUserId() + " не найден"));

        login.setRole(assignRoleDTO.getRole());
        loginRepository.save(login);
    }
}
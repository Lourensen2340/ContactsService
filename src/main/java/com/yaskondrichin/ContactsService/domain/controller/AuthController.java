package com.yaskondrichin.ContactsService.domain.controller;

import com.yaskondrichin.ContactsService.DTO.AuthResponseDTO;
import com.yaskondrichin.ContactsService.DTO.LoginRequestDTO;
import com.yaskondrichin.ContactsService.DTO.LoginResponseDTO;
import com.yaskondrichin.ContactsService.Mapper.LoginMapper;
import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;
    private final LoginMapper loginMapper; // Добавьте это поле

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(loginService.register(dto));
    }

    @GetMapping("/me")
    @Operation(summary = "Получить профиль текущего пользователя")
    public ResponseEntity<LoginResponseDTO> getMyProfile(@AuthenticationPrincipal Jwt jwt) {
        Login user = loginService.getMe(jwt);
        return ResponseEntity.ok(loginMapper.toResponseDto(user));
    }
    @PostMapping("/exchange")
    public ResponseEntity<AuthResponseDTO> exchangeIdForTokens(@RequestParam Long userId) {
        AuthResponseDTO response = loginService.generateTokensByUserId(userId);
        return ResponseEntity.ok(response);
    }

}
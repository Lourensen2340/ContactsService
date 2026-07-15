package com.yaskondrichin.ContactsService.domain.controller;

import com.yaskondrichin.ContactsService.DTO.*;
import com.yaskondrichin.ContactsService.Mapper.LoginMapper;
import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.repo.LoginRepository;
import com.yaskondrichin.ContactsService.service.AuthService;
import com.yaskondrichin.ContactsService.service.JwtService;
import com.yaskondrichin.ContactsService.service.LoginService;
import com.yaskondrichin.ContactsService.DTO.RegisterRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final LoginRepository loginRepository;
    private final JwtService jwtService;
    private final AuthService authService;
//    @PostMapping("/login")
//    public ResponseEntity<TokenResponseDTO> login(@RequestBody LoginRequestDTO request) {
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword())
//        );
//
//        // 2. Если проверка прошла успешно, достаем пользователя из БД для генерации токена
//        Login user = loginRepository.findByLogin(request.getLogin())
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//        // 3. Генерируем пару токенов с claims внутри вашей JwtServise
//        TokenResponseDTO tokens = jwtService.generateTokens(user);
//
//        return ResponseEntity.ok(tokens);
//    }

    private final LoginService loginService;
    private final LoginMapper loginMapper; // Добавьте это поле
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterDTO dto) {
        // 1. Регистрируем пользователя (пароль генерируется внутри AuthServiceImpl)
        Login registeredLogin = authService.register(dto);

        // 2. Генерируем JWT-токены для мгновенного входа (если применимо в вашей архитектуре)
        TokenResponseDTO tokens = jwtService.generateTokens(registeredLogin);

        // 3. Формируем ответ, вшивая туда сгенерированный пароль из временного поля
        AuthResponseDTO response = AuthResponseDTO.builder()
                .user(loginMapper.toResponseDto(registeredLogin))
                .tokens(tokens)
                .generatedPassword(registeredLogin.getRawPassword()) // Забираем сырой пароль!
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Получить профиль текущего пользователя")
    public ResponseEntity<LoginResponseDTO> getMyProfile(@AuthenticationPrincipal Jwt jwt) {
        Login user = loginService.getMe(jwt);
        return ResponseEntity.ok(loginMapper.toResponseDto(user));
    }
    @PostMapping("/exchange")
    public ResponseEntity<AuthResponseDTO> exchangeIdForTokens(@RequestParam UUID userId) {
        AuthResponseDTO response = jwtService.generateTokensByUserId(userId);
        return ResponseEntity.ok(response);
    }

}
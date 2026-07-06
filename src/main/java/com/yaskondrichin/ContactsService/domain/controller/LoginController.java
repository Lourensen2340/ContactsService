package com.yaskondrichin.ContactsService.domain.controller;

import com.yaskondrichin.ContactsService.Mapper.LoginMapper;
import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.repo.LoginRepository;
import com.yaskondrichin.ContactsService.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.UUID; // ДОБАВЛЕНО

@RestController
@RequestMapping("/api/v1/logins")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final LoginMapper loginMapper;
    private final LoginRepository loginRepository;

    @GetMapping("/me")
    public ResponseEntity<?> getMe(@AuthenticationPrincipal Jwt jwt) {
        Login user = loginService.getMe(jwt);
        return ResponseEntity.ok(loginMapper.toResponseDto(user));
    }

    @DeleteMapping("/{id}") // ИСПРАВЛЕНО: Добавлена аннотация маппинга пути
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) { // ИСПРАВЛЕНО: Long -> UUID
        Login login = loginRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь с ID " + id + " не найден"));

        loginRepository.delete(login);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/debug")
    public String debug(@AuthenticationPrincipal Jwt jwt) {
        return "Your ID from token (sub): " + jwt.getSubject();
    }
}
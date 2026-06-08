package com.yaskondrichin.ContactsService.domain.controller;

import com.yaskondrichin.ContactsService.DTO.AuthResponseDTO;
import com.yaskondrichin.ContactsService.DTO.LoginRequestDTO;
import com.yaskondrichin.ContactsService.DTO.LoginResponseDTO;
import com.yaskondrichin.ContactsService.Mapper.LoginMapper;
import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.repo.LoginRepository;
import com.yaskondrichin.ContactsService.domain.repo.UserRepository;
import com.yaskondrichin.ContactsService.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/logins")
@RequiredArgsConstructor // Добавьте это для автоматической инъекции сервиса и маппера
public class LoginController {

    private final LoginService loginService;
    private final LoginMapper loginMapper;
    private final LoginRepository loginRepository;

    @GetMapping("/me")
    public ResponseEntity<?> getMe(@AuthenticationPrincipal Jwt jwt) {

        Login user = loginService.getMe(jwt);
      
        return ResponseEntity.ok(loginMapper.toResponseDto(user));
    }



    @Transactional
    public void delete(Long id) {
        // Находим пользователя или сразу выбрасываем ошибку, если его нет
        Login login = loginRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь с ID " + id + " не найден"));

        // Удаляем объект
        loginRepository.delete(login);
    }


    @GetMapping("/debug")
    public String debug(@AuthenticationPrincipal Jwt jwt) {
        return "Your ID from token (sub): " + jwt.getSubject();
    }

}
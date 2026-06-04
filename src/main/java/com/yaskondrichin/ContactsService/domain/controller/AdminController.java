package com.yaskondrichin.ContactsService.domain.controller;

import com.yaskondrichin.ContactsService.DTO.AssignRoleDTO;
import com.yaskondrichin.ContactsService.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Абсолютно все методы этого контроллера доступны ТОЛЬКО админу
public class AdminController {

    private final LoginService loginService;

    @PutMapping("/assign-role")
    public ResponseEntity<String> assignRole(@Valid @RequestBody AssignRoleDTO assignRoleDTO) {

        loginService.assignRole(assignRoleDTO);

        return ResponseEntity.ok("Роль успешно изменена профилю с ID " + assignRoleDTO.getUserId());
    }
}

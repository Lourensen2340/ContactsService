package com.yaskondrichin.ContactsService.service;

import com.yaskondrichin.ContactsService.DTO.*;
import com.yaskondrichin.ContactsService.domain.model.Login;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface LoginService {

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    LoginResponseDTO getLastCreatedUser();

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    Login getMe(Jwt jwt);

    @PreAuthorize("hasRole('ADMIN') or #id.toString().equals(authentication.name)")
    @Transactional(readOnly = true)
    LoginResponseDTO findById(UUID id);

    AuthResponseDTO generateTokensByUserId(UUID userId);

    @Transactional
    void assignRole(AssignRoleDTO assignRoleDTO);
}
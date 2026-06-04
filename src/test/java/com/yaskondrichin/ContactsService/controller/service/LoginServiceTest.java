package com.yaskondrichin.ContactsService.controller.service;


import com.yaskondrichin.ContactsService.DTO.AssignRoleDTO;
import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.model.Role;
import com.yaskondrichin.ContactsService.domain.repo.LoginRepository;
import com.yaskondrichin.ContactsService.service.LoginService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class LoginServiceTest {

    @Mock
    private LoginRepository loginRepository;

    @InjectMocks
    private LoginService loginService;

    @Test
    public void assignRole_WhenUserExists_ShouldUpdateRole() {
        AssignRoleDTO dto = new AssignRoleDTO();
        dto.setUserId(1L);
        dto.setRole(Role.ROLE_ADMIN);

        Login existingLogin = new Login();
        existingLogin.setId(1L);
        existingLogin.setRole(Role.USER);

        Mockito.when(loginRepository.findById(1L)).thenReturn(Optional.of(existingLogin));
        Mockito.when(loginRepository.save(Mockito.any(Login.class))).thenAnswer(invocation -> invocation.getArgument(0));

        loginService.assignRole(dto);

        Assertions.assertEquals(Role.ROLE_ADMIN, existingLogin.getRole());
        Mockito.verify(loginRepository, Mockito.times(1)).save(existingLogin);
    }
}

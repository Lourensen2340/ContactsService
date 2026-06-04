package com.yaskondrichin.ContactsService.controller.service;

import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.model.Role;
import com.yaskondrichin.ContactsService.domain.repo.LoginRepository;
import com.yaskondrichin.ContactsService.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private LoginRepository loginRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        // 1. Подготовка
        Login login = new Login();
        login.setLogin("testUser");
        login.setPass("encodedPassword");
        login.setRole(Role.USER);

        when(loginRepository.findByLogin("testUser")).thenReturn(Optional.of(login));

        // 2. Действие
        UserDetails userDetails = userDetailsService.loadUserByUsername("testUser");

        // 3. Проверка
        assertNotNull(userDetails);
        assertEquals("testUser", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {
        // 1. Подготовка
        when(loginRepository.findByLogin("unknownUser")).thenReturn(Optional.empty());

        // 2. Действие и Проверка
        assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserByUsername("unknownUser")
        );
    }
}

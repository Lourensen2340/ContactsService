package com.yaskondrichin.ContactsService.service.impl;

import com.yaskondrichin.ContactsService.DTO.RegisterDTO;
import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.model.User;
import com.yaskondrichin.ContactsService.domain.model.Role;
import com.yaskondrichin.ContactsService.domain.repo.LoginRepository;
import com.yaskondrichin.ContactsService.domain.repo.UserRepository;
import com.yaskondrichin.ContactsService.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor // Здесь эта аннотация РАЗРЕШЕНА, так как это class
public class AuthServiceImpl implements AuthService {

    // Здесь модификаторы private final РАЗРЕШЕНЫ
    private final LoginRepository loginRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Login register(RegisterDTO dto) { // Здесь тело метода {...} РАЗРЕШЕНО
        // 1. Создаем и сохраняем запись в таблицу 'login'
        Login login = new Login();
        login.setLogin(dto.getUsername());
        login.setPass(passwordEncoder.encode(dto.getPassword()));
        login.setEmail(dto.getEmail());
        login.setPhone(dto.getPhone());
        login.setRole(Role.USER);

        Login savedLogin = loginRepository.save(login);

        // 2. Создаем и сохраняем запись в таблицу 'users'
        User user = new User();
        user.setId(savedLogin.getId());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(savedLogin.getPass());

        userRepository.save(user);
        return savedLogin;
    }
}

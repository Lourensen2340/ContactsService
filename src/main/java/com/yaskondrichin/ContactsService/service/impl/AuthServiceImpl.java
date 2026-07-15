package com.yaskondrichin.ContactsService.service.impl;

import com.yaskondrichin.ContactsService.DTO.RegisterDTO;
import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.enums.Role;
import com.yaskondrichin.ContactsService.domain.repo.LoginRepository;
import com.yaskondrichin.ContactsService.service.AuthService;
import com.yaskondrichin.ContactsService.Utils.PasswordGenerator; // Новый импорт
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final LoginRepository loginRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordGenerator passwordGenerator; // Внедряем генератор из domain.generator

    @Override
    @Transactional
    public Login register(RegisterDTO dto) {
        // 1. Генерируем пароль с помощью нашего доменного генератора
        String randomPassword = passwordGenerator.generateRandomPassword();

        Login login = new Login();
        login.setLogin(dto.getUsername());

        // 2. Хэшируем именно сгенерированный пароль
        login.setPass(passwordEncoder.encode(randomPassword));

        login.setEmail(dto.getEmail());
        login.setPhone(dto.getPhone());
        login.setRole(Role.USER);

        // 3. Запоминаем сгенерированный пароль в transient-поле для контроллера
        login.setRawPassword(randomPassword);

        return loginRepository.save(login);
    }
}

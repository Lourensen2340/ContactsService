package com.yaskondrichin.ContactsService.service;

import com.yaskondrichin.ContactsService.DTO.RegisterDTO;
import com.yaskondrichin.ContactsService.domain.model.Login;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    Login register(RegisterDTO dto);


}
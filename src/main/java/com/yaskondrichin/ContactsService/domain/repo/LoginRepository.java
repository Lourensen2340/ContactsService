package com.yaskondrichin.ContactsService.domain.repo;

import com.yaskondrichin.ContactsService.domain.model.Login;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

// ВАЖНО: Проверьте наличие <Login, Long>
public interface LoginRepository extends JpaRepository<Login, UUID> {
    Optional<Login> findByLogin(String login);
    Optional<Login> findFirstByOrderByIdDesc();


}


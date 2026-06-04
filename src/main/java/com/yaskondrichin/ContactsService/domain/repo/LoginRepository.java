package com.yaskondrichin.ContactsService.domain.repo;

import com.yaskondrichin.ContactsService.domain.model.Login;
import org.springframework.core.MethodParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Optional;

// ВАЖНО: Проверьте наличие <Login, Long>
public interface LoginRepository extends JpaRepository<Login, Long> {
    Optional<Login> findByLogin(String login);
    Optional<Login> findFirstByOrderByIdDesc();


}


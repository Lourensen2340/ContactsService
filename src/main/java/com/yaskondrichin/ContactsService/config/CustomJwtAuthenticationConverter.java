package com.yaskondrichin.ContactsService.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class CustomJwtAuthenticationConverter extends JwtAuthenticationConverter {

    public CustomJwtAuthenticationConverter() {
        this.setJwtGrantedAuthoritiesConverter(jwt -> {
            // "roles" — это тот самый claim, который вы указали в JwtService
            String roleName = jwt.getClaimAsString("roles");
            if (roleName == null) {
                return Collections.emptyList();
            }
            // Spring Security ожидает префикс ROLE_ для методов вроде .hasRole("USER")
            String formattedRole = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;
            return List.of(new SimpleGrantedAuthority(formattedRole));
        });
    }
}

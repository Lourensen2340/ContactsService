package com.yaskondrichin.ContactsService.config;

// ВАЖНО: Добавляем импорт интерфейса Spring
import com.yaskondrichin.ContactsService.config.argument_resolver.impl.AuthenticatedUserIdResolver;
import com.yaskondrichin.ContactsService.config.argument_resolver.impl.LoggedInUserIdArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import java.util.List;

@Configuration
@RequiredArgsConstructor

public class WebConfig implements WebMvcConfigurer {

    private final AuthenticatedUserIdResolver authenticatedUserIdResolver;
    private final LoggedInUserIdArgumentResolver loggedInUserIdArgumentResolver;
    // ВАЖНО: Имя метода должно быть строго addArgumentResolvers, и он должен быть public
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authenticatedUserIdResolver);


    }
}
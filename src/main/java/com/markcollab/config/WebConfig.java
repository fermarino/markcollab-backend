package com.markcollab.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Permite todas as rotas
                .allowedOrigins("*") // Permite todas as origens
                .allowedMethods("*") // Permite todos os métodos (GET, POST, PUT, DELETE, etc.)
                .allowedHeaders("*") // Permite todos os cabeçalhos
                .allowCredentials(false); // Desabilita o uso de cookies/autenticação via credenciais
    }
}

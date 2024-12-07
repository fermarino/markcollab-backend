package com.markcollab.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Permite todas as rotas
                .allowedOrigins("*") // Permite todas as origens
                .allowedMethods("*") // Permite todos os métodos HTTP
                .allowedHeaders("*") // Permite todos os cabeçalhos
                .exposedHeaders("*") // Exibe todos os cabeçalhos
                .allowCredentials(false); // Cookies desabilitados
    }
}

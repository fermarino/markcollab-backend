package com.markcollab.config;

import com.markcollab.repository.EmployerRepository;
import com.markcollab.repository.FreelancerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    // As injeções de dependência podem permanecer, caso você adicione
    // outros beans não relacionados à segurança aqui no futuro.
    private final EmployerRepository employerRepo;
    private final FreelancerRepository freelancerRepo;

    // TODOS OS MÉTODOS @Bean FORAM REMOVIDOS DESTE ARQUIVO.
    // A definição deles agora existe somente em SecurityConfig.java

}

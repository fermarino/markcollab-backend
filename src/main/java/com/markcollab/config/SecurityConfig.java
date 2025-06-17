package com.markcollab.config;

import com.markcollab.repository.EmployerRepository;
import com.markcollab.repository.FreelancerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final EmployerRepository employerRepository;
    private final FreelancerRepository freelancerRepository;
    private final JwtAuthenticationFilter jwtAuthFilter;

    // Injete a URL do seu frontend a partir do application.properties
    @Value("${frontend.base-url}")
    private String frontendUrl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            // Informa ao Spring Security para usar a configuração de CORS definida no bean "corsConfigurationSource"
            .cors(withDefaults())
            .authorizeHttpRequests(auth -> auth
                // 1. Permite requisições OPTIONS (preflight CORS) para qualquer caminho
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // 2. Rotas públicas (autenticação e webhooks)
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/webhooks/**").permitAll() // <-- Caminho do webhook corrigido
                
                // 3. Todas as outras requisições exigem autenticação
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // ERRO ESTAVA AQUI: A origem permitida deve ser a URL do seu FRONTEND
        configuration.setAllowedOrigins(List.of(frontendUrl, "http://localhost:5173"));
        
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return identifier -> {
            // A busca por múltiplos campos continua a mesma
            return employerRepository.findByEmail(identifier)
                    .map(user -> (UserDetails) user)
                    .or(() -> employerRepository.findByUsername(identifier).map(user -> (UserDetails) user))
                    .or(() -> freelancerRepository.findByEmail(identifier).map(user -> (UserDetails) user))
                    .or(() -> freelancerRepository.findByUsername(identifier).map(user -> (UserDetails) user))
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + identifier));
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
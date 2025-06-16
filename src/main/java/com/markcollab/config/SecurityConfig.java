package com.markcollab.config;

import com.markcollab.model.Employer;
import com.markcollab.model.Freelancer;
import com.markcollab.repository.EmployerRepository;
import com.markcollab.repository.FreelancerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer; // Manter import, mas o bean será removido
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
import java.util.Optional;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final EmployerRepository employerRepository;
    private final FreelancerRepository freelancerRepository;

    // REMOVA ESTE BEAN INTEIRO (WebSecurityCustomizer)
    /*
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                "/api/mercadopago/webhook"
        );
    }
    */

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthFilter,
            AuthenticationProvider authenticationProvider
    ) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF já está desabilitado
                .cors(withDefaults())
                .authorizeHttpRequests(auth -> auth
                        // 1. **PRIORIDADE MÁXIMA:** Permite acesso total ao endpoint do webhook do Mercado Pago.
                        .requestMatchers("/api/mercadopago/webhook").permitAll() // <--- De volta aqui e como a primeira regra
                        // 2. Permite requisições OPTIONS (preflight CORS) para qualquer caminho
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 3. Rotas públicas (autenticação e pagamento)
                        .requestMatchers("/api/auth/**", "/api/projects/*/pay/*").permitAll()
                        // 4. Todas as outras requisições exigem autenticação
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return identifier -> {
            return employerRepository.findByEmail(identifier)
                    .map(user -> (UserDetails) user)
                    .or(() -> employerRepository.findById(identifier).map(user -> (UserDetails) user))
                    .or(() -> employerRepository.findByUsername(identifier).map(user -> (UserDetails) user))
                    .or(() -> freelancerRepository.findByEmail(identifier).map(user -> (UserDetails) user))
                    .or(() -> freelancerRepository.findById(identifier).map(user -> (UserDetails) user))
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

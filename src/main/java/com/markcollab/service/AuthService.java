package com.markcollab.service;

import com.markcollab.model.Employer;
import com.markcollab.model.Freelancer;
import com.markcollab.repository.EmployerRepository;
import com.markcollab.repository.FreelancerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private EmployerRepository employerRepository;

    @Autowired
    private FreelancerRepository freelancerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    /**
     * Método para registrar um empregador (Employer).
     */
    public void registerEmployer(Map<String, Object> body) {
        String cpf = (String) body.get("cpf");
        String email = (String) body.get("email");
        String username = (String) body.get("username");

        // Valida duplicidade de CPF
        if (employerRepository.existsById(cpf)) {
            throw new RuntimeException("CPF já em uso.");
        }

        // Valida duplicidade de e-mail
        if (employerRepository.existsByEmail(email)) {
            throw new RuntimeException("E-mail já está em uso.");
        }

        // Valida duplicidade de username
        if (employerRepository.existsByUsername(username)) {
            throw new RuntimeException("Username já está em uso.");
        }

        Employer employer = new Employer();
        employer.setCpf(cpf);
        employer.setName((String) body.get("name"));
        employer.setUsername(username);
        employer.setEmail(email);
        employer.setPassword(passwordEncoder.encode((String) body.get("password")));
        employer.setCompanyName((String) body.get("companyName"));
        employer.setRole("EMPLOYER");

        employerRepository.save(employer);
    }

    /**
     * Método para registrar um freelancer.
     */
    public void registerFreelancer(Map<String, Object> body) {
        String cpf = (String) body.get("cpf");
        String email = (String) body.get("email");
        String username = (String) body.get("username");

        // Valida duplicidade de CPF
        if (freelancerRepository.existsById(cpf)) {
            throw new RuntimeException("CPF já em uso.");
        }

        // Valida duplicidade de e-mail
        if (freelancerRepository.existsByEmail(email)) {
            throw new RuntimeException("E-mail já está em uso.");
        }

        // Valida duplicidade de username
        if (freelancerRepository.existsByUsername(username)) {
            throw new RuntimeException("Username já está em uso.");
        }

        Freelancer freelancer = new Freelancer();
        freelancer.setCpf(cpf);
        freelancer.setName((String) body.get("name"));
        freelancer.setUsername(username);
        freelancer.setEmail(email);
        freelancer.setPassword(passwordEncoder.encode((String) body.get("password")));
        freelancer.setPortfolioLink((String) body.get("portfolioLink"));
        freelancer.setRole("FREELANCER");

        freelancerRepository.save(freelancer);
    }

    /**
     * Método para autenticar um usuário baseado no username e senha.
     */
    public String authenticate(String username, String password) {
        // Tenta autenticar como Employer
        Employer employer = employerRepository.findByUsername(username).orElse(null);
        if (employer != null && passwordEncoder.matches(password, employer.getPassword())) {
            return jwtService.generateToken(employer);
        }

        // Tenta autenticar como Freelancer
        Freelancer freelancer = freelancerRepository.findByUsername(username).orElse(null);
        if (freelancer != null && passwordEncoder.matches(password, freelancer.getPassword())) {
            return jwtService.generateToken(freelancer);
        }

        throw new RuntimeException("Invalid username or password.");
    }
}

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

    public void registerFreelancer(Map<String, Object> body) {
        String cpf = (String) body.get("cpf");
        String email = (String) body.get("email");
        String username = (String) body.get("username");

        if (freelancerRepository.existsById(cpf) ||
                freelancerRepository.existsByEmail(email) ||
                freelancerRepository.existsByUsername(username)) {
            throw new RuntimeException("Dados do freelancer já estão em uso.");
        }

        Freelancer freelancer = new Freelancer();
        freelancer.setCpf(cpf);
        freelancer.setName((String) body.get("name"));
        freelancer.setUsername(username);
        freelancer.setEmail(email);
        freelancer.setPassword(passwordEncoder.encode((String) body.get("password")));
        freelancer.setRole("FREELANCER");

        freelancerRepository.save(freelancer);
    }

    public void registerEmployer(Map<String, Object> body) {
        String cpf = (String) body.get("cpf");
        String email = (String) body.get("email");
        String username = (String) body.get("username");

        if (employerRepository.existsById(cpf) ||
                employerRepository.existsByEmail(email) ||
                employerRepository.existsByUsername(username)) {
            throw new RuntimeException("Dados do contratante já estão em uso.");
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

    public String authenticate(String identifier, String password) {
        // Buscar EMPLOYER por email, cpf ou username
        Employer employer = employerRepository.findByEmail(identifier)
                .or(() -> employerRepository.findById(identifier))
                .or(() -> employerRepository.findByUsername(identifier))
                .orElse(null);

        if (employer != null && passwordEncoder.matches(password, employer.getPassword())) {
            return jwtService.generateToken(employer);
        }

        // Buscar FREELANCER por email, cpf ou username
        Freelancer freelancer = freelancerRepository.findByEmail(identifier)
                .or(() -> freelancerRepository.findById(identifier))
                .or(() -> freelancerRepository.findByUsername(identifier))
                .orElse(null);

        if (freelancer != null && passwordEncoder.matches(password, freelancer.getPassword())) {
            return jwtService.generateToken(freelancer);
        }

        throw new RuntimeException("Credenciais inválidas.");
    }

    public String getCpf(String identifier) {
        return employerRepository.findByEmail(identifier)
                .or(() -> employerRepository.findById(identifier))
                .or(() -> employerRepository.findByUsername(identifier))
                .map(Employer::getCpf)
                .orElseGet(() ->
                        freelancerRepository.findByEmail(identifier)
                                .or(() -> freelancerRepository.findById(identifier))
                                .or(() -> freelancerRepository.findByUsername(identifier))
                                .map(Freelancer::getCpf)
                                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."))
                );
    }
}

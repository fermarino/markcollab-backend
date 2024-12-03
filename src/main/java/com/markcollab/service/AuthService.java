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

    public void registerEmployer(Map<String, Object> body) {
        Employer employer = new Employer();
        employer.setCpf((String) body.get("cpf")); // Setar CPF manualmente
        employer.setName((String) body.get("name"));
        employer.setUsername((String) body.get("username"));
        employer.setEmail((String) body.get("email"));
        employer.setPassword(passwordEncoder.encode((String) body.get("password")));
        employer.setCompanyName((String) body.get("companyName"));
        employerRepository.save(employer); // Salvar após atribuir CPF
    }

    public void registerFreelancer(Map<String, Object> body) {
        Freelancer freelancer = new Freelancer();
        freelancer.setCpf((String) body.get("cpf")); // Setando CPF manualmente
        freelancer.setName((String) body.get("name"));
        freelancer.setUsername((String) body.get("username"));
        freelancer.setEmail((String) body.get("email"));
        freelancer.setPassword(passwordEncoder.encode((String) body.get("password")));
        freelancer.setPortfolioLink((String) body.get("portfolioLink")); // Campo específico do freelancer
        freelancerRepository.save(freelancer); // Salva após garantir CPF
    }


    public String authenticate(String username, String password) {
        Employer employer = employerRepository.findByUsername(username).orElse(null);
        if (employer != null && passwordEncoder.matches(password, employer.getPassword())) {
            return jwtService.generateToken(employer);
        }

        Freelancer freelancer = freelancerRepository.findByUsername(username).orElse(null);
        if (freelancer != null && passwordEncoder.matches(password, freelancer.getPassword())) {
            return jwtService.generateToken(freelancer);
        }

        throw new RuntimeException("Invalid username or password");
    }
}

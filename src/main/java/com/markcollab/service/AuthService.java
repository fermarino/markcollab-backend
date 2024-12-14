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
        String cpf = (String) body.get("cpf");
        String email = (String) body.get("email");
        String username = (String) body.get("username");

        if (employerRepository.existsById(cpf) || employerRepository.existsByEmail(email) || employerRepository.existsByUsername(username)) {
            throw new RuntimeException("Employer details already in use.");
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

    public void registerFreelancer(Map<String, Object> body) {
        String cpf = (String) body.get("cpf");
        String email = (String) body.get("email");
        String username = (String) body.get("username");

        if (freelancerRepository.existsById(cpf) || freelancerRepository.existsByEmail(email) || freelancerRepository.existsByUsername(username)) {
            throw new RuntimeException("Freelancer details already in use.");
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

    public String getCpf(String username) {
    Employer employer = employerRepository.findByUsername(username).orElse(null);
    if (employer != null) {
        return employer.getCpf();
    }

    Freelancer freelancer = freelancerRepository.findByUsername(username).orElse(null);
    if (freelancer != null) {
        return freelancer.getCpf();
    }

    throw new RuntimeException("User not found.");
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

        throw new RuntimeException("Invalid username or password.");
    }
}

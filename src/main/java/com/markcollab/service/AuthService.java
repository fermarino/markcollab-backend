package com.markcollab.service;

import com.markcollab.model.Employer;
import com.markcollab.model.Freelancer;
import com.markcollab.repository.EmployerRepository;
import com.markcollab.repository.FreelancerRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
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
        employer.setName((String) body.get("name"));
        employer.setUsername((String) body.get("username"));
        employer.setEmail((String) body.get("email"));
        employer.setPassword(passwordEncoder.encode((String) body.get("password")));
        employer.setCompanyName((String) body.get("companyName"));
        employerRepository.save(employer);
    }

    public void registerFreelancer(Map<String, Object> body) {
        Freelancer freelancer = new Freelancer();
        freelancer.setName((String) body.get("name"));
        freelancer.setUsername((String) body.get("username"));
        freelancer.setEmail((String) body.get("email"));
        freelancer.setPassword(passwordEncoder.encode((String) body.get("password")));
        freelancer.setPortfolioLink((String) body.get("portfolioLink"));
        freelancerRepository.save(freelancer);
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

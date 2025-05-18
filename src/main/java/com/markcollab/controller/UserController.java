package com.markcollab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.markcollab.model.Employer;
import com.markcollab.model.Freelancer;
import com.markcollab.repository.EmployerRepository;
import com.markcollab.repository.FreelancerRepository;
import com.markcollab.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private FreelancerRepository freelancerRepository;

    @Autowired
    private EmployerRepository employerRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/me")
    public ResponseEntity<?> getLoggedUser(HttpServletRequest request) {
        String cpf = extractCpfFromRequest(request);
        if (cpf == null) return ResponseEntity.status(401).body("Token inválido");

        return freelancerRepository.findById(cpf)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .or(() -> employerRepository.findById(cpf).map(ResponseEntity::ok))
                .orElse(ResponseEntity.status(404).body("Usuário não encontrado"));
    }

    @PutMapping("/me/update")
    public ResponseEntity<?> updateLoggedUser(HttpServletRequest request, @RequestBody Map<String, Object> data) {
        String cpf = extractCpfFromRequest(request);
        if (cpf == null) return ResponseEntity.status(401).body("Token inválido");

        if (freelancerRepository.findById(cpf).isPresent()) {
            Freelancer existing = freelancerRepository.findById(cpf).get();
            Freelancer updated = objectMapper.convertValue(data, Freelancer.class);

            existing.setName(updated.getName());
            existing.setUsername(updated.getUsername());
            existing.setEmail(updated.getEmail());
            existing.setAboutMe(updated.getAboutMe());
            existing.setExperience(updated.getExperience());

            return ResponseEntity.ok(freelancerRepository.save(existing));
        } else if (employerRepository.findById(cpf).isPresent()) {
            Employer existing = employerRepository.findById(cpf).get();
            Employer updated = objectMapper.convertValue(data, Employer.class);

            existing.setName(updated.getName());
            existing.setUsername(updated.getUsername());
            existing.setEmail(updated.getEmail());
            existing.setAboutMe(updated.getAboutMe());
            existing.setCompanyName(updated.getCompanyName());

            return ResponseEntity.ok(employerRepository.save(existing));
        }

        return ResponseEntity.status(404).body("Usuário não encontrado");
    }

    private String extractCpfFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        String token = authHeader.substring(7);
        return jwtService.extractCpf(token);
    }
}

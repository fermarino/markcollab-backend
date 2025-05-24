package com.markcollab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.markcollab.exception.UnauthorizedException;
import com.markcollab.model.Employer;
import com.markcollab.model.Freelancer;
import com.markcollab.repository.EmployerRepository;
import com.markcollab.repository.FreelancerRepository;
import com.markcollab.service.JwtService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired private FreelancerRepository freelancerRepo;
    @Autowired private EmployerRepository employerRepo;
    @Autowired private JwtService jwtService;
    @Autowired private ObjectMapper objectMapper;

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpServletRequest req) {
        String cpf = extractCpf(req);
        return freelancerRepo.findById(cpf)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .or(() -> employerRepo.findById(cpf).map(ResponseEntity::ok))
                .orElseThrow(() -> new UnauthorizedException("Usuário não encontrado."));
    }

    @PutMapping("/me/update")
    public ResponseEntity<?> updateMe(HttpServletRequest req,
                                      @Valid @RequestBody Map<String,Object> data) {
        String cpf = extractCpf(req);

        if (freelancerRepo.existsById(cpf)) {
            Freelancer ex = freelancerRepo.findById(cpf).get();
            Freelancer up = objectMapper.convertValue(data, Freelancer.class);
            ex.setName(up.getName());
            ex.setUsername(up.getUsername());
            ex.setEmail(up.getEmail());
            ex.setAboutMe(up.getAboutMe());
            ex.setExperience(up.getExperience());
            return ResponseEntity.ok(freelancerRepo.save(ex));
        }

        if (employerRepo.existsById(cpf)) {
            Employer ex = employerRepo.findById(cpf).get();
            Employer up = objectMapper.convertValue(data, Employer.class);
            ex.setName(up.getName());
            ex.setUsername(up.getUsername());
            ex.setEmail(up.getEmail());
            ex.setAboutMe(up.getAboutMe());
            ex.setCompanyName(up.getCompanyName());
            return ResponseEntity.ok(employerRepo.save(ex));
        }

        throw new UnauthorizedException("Usuário não encontrado.");
    }

    private String extractCpf(HttpServletRequest req) {
        String h = req.getHeader("Authorization");
        if (h==null||!h.startsWith("Bearer ")) throw new UnauthorizedException("Token inválido.");
        return jwtService.extractCpf(h.substring(7));
    }
}

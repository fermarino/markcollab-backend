package com.markcollab.service;

import com.markcollab.dto.EmployerDTO;
import com.markcollab.model.Employer;
import com.markcollab.repository.EmployerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployerService {

    @Autowired
    private EmployerRepository employerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public EmployerDTO registerEmployer(Employer employer) {
        validateEmployer(employer);
        validatePassword(employer.getPassword());
        employer.setRole("EMPLOYER");
        employer.setPassword(passwordEncoder.encode(employer.getPassword()));
        Employer saved = employerRepository.save(employer);

        EmployerDTO dto = new EmployerDTO();
        dto.setName(saved.getName());
        dto.setUsername(saved.getUsername());
        dto.setEmail(saved.getEmail());
        dto.setCompanyName(saved.getCompanyName());
        dto.setAboutMe(saved.getAboutMe());
        return dto;
    }

    public Employer updateEmployer(String cpf, Employer updated) {
        Employer e = employerRepository.findById(cpf)
                .orElseThrow(() -> new RuntimeException("Employer not found"));
        e.setName(updated.getName());
        e.setUsername(updated.getUsername());
        e.setEmail(updated.getEmail());
        e.setCompanyName(updated.getCompanyName());
        e.setAboutMe(updated.getAboutMe());
        return employerRepository.save(e);
    }

    public void deleteEmployer(String cpf) {
        Employer e = employerRepository.findById(cpf)
                .orElseThrow(() -> new RuntimeException("Employer not found"));
        employerRepository.delete(e);
    }

    public List<EmployerDTO> getAllEmployers() {
        return employerRepository.findAll().stream().map(saved -> {
            EmployerDTO dto = new EmployerDTO();
            dto.setName(saved.getName());
            dto.setUsername(saved.getUsername());
            dto.setEmail(saved.getEmail());
            dto.setCompanyName(saved.getCompanyName());
            dto.setAboutMe(saved.getAboutMe());
            return dto;
        }).collect(Collectors.toList());
    }

    private void validateEmployer(Employer e) {
        if (employerRepository.existsByEmail(e.getEmail()))
            throw new RuntimeException("Email already in use");
        if (employerRepository.existsById(e.getCpf()))
            throw new RuntimeException("CPF already in use");
    }

    private void validatePassword(String pw) {
        if (pw.length() < 8) throw new RuntimeException("Password must be at least 8 characters long");
        if (!pw.matches(".*[A-Z].*")) throw new RuntimeException("Password must contain at least one uppercase letter");
        if (!pw.matches(".*\\d.*")) throw new RuntimeException("Password must contain at least one number");
    }
}

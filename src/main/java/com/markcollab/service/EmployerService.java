package com.markcollab.service;

import com.markcollab.dto.EmployerDTO;
import com.markcollab.model.Employer;
import com.markcollab.repository.EmployerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployerService {

    @Autowired
    private EmployerRepository employerRepository;


    @Autowired
    AuthService authService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public EmployerDTO registerEmployer(Employer employer) {
        validatePassword(employer.getPassword());
        validateEmployer(employer);
        employer.setRole("EMPLOYER");
        employer.setPassword(passwordEncoder.encode(employer.getPassword()));
        Employer savedEmployer = employerRepository.save(employer);

        EmployerDTO dto = new EmployerDTO();
        dto.setName(savedEmployer.getName());
        dto.setUsername(savedEmployer.getUsername());
        dto.setEmail(savedEmployer.getEmail());
        dto.setCompanyName(savedEmployer.getCompanyName());
        return dto;
    }

    public Employer updateEmployer(String cpf, Employer updatedEmployer) {
        Employer employer = employerRepository.findById(cpf)
                .orElseThrow(() -> new RuntimeException("Employer not found"));
        employer.setName(updatedEmployer.getName());
        employer.setUsername(updatedEmployer.getUsername());
        employer.setEmail(updatedEmployer.getEmail());
        employer.setCompanyName(updatedEmployer.getCompanyName());
        return employerRepository.save(employer);
    }

    public void deleteEmployer(String cpf) {
        Employer employer = employerRepository.findById(cpf)
                .orElseThrow(() -> new RuntimeException("Employer not found"));
        employerRepository.delete(employer);
    }

    public List<Employer> getAllEmployers() {
        return employerRepository.findAll();
    }

    private void validateEmployer(Employer employer) {
        if (employerRepository.existsByUsername(employer.getUsername())) {
            throw new RuntimeException("Username already in use");
        }
        if (employerRepository.existsByEmail(employer.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        if (employerRepository.existsById(employer.getCpf())) {
            throw new RuntimeException("CPF already in use");
        }
    }
}

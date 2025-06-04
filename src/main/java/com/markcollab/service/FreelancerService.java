// src/main/java/com/markcollab/service/FreelancerService.java
package com.markcollab.service;

import com.markcollab.dto.FreelancerDTO;
import com.markcollab.model.Freelancer;
import com.markcollab.repository.FreelancerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FreelancerService {

    @Autowired
    private FreelancerRepository freelancerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public FreelancerDTO registerFreelancer(Freelancer freelancer) {
        validateFreelancer(freelancer);
        validatePassword(freelancer.getPassword());
        freelancer.setRole("FREELANCER");
        freelancer.setPassword(passwordEncoder.encode(freelancer.getPassword()));
        Freelancer saved = freelancerRepository.save(freelancer);
        return mapToDTO(saved);
    }

    public Freelancer updateFreelancer(String cpf, Freelancer updated) {
        Freelancer f = freelancerRepository.findById(cpf)
                .orElseThrow(() -> new RuntimeException("Freelancer not found"));
        f.setName(updated.getName());
        f.setUsername(updated.getUsername());
        f.setEmail(updated.getEmail());
        f.setPortfolioLink(updated.getPortfolioLink());
        f.setAboutMe(updated.getAboutMe());
        f.setExperience(updated.getExperience());
        return freelancerRepository.save(f);
    }

    public void deleteFreelancer(String cpf) {
        Freelancer f = freelancerRepository.findById(cpf)
                .orElseThrow(() -> new RuntimeException("Freelancer not found"));
        freelancerRepository.delete(f);
    }

    public List<FreelancerDTO> getAllFreelancers() {
        return freelancerRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public FreelancerDTO getFreelancerByCpf(String cpf) {
        Freelancer f = freelancerRepository.findById(cpf)
                .orElseThrow(() -> new RuntimeException("Freelancer not found"));
        return mapToDTO(f);
    }

    private FreelancerDTO mapToDTO(Freelancer f) {
        FreelancerDTO dto = new FreelancerDTO();
        dto.setName(f.getName());
        dto.setUsername(f.getUsername());
        dto.setEmail(f.getEmail());
        dto.setPortfolioLink(f.getPortfolioLink());
        dto.setAboutMe(f.getAboutMe());
        dto.setExperience(f.getExperience());
        return dto;
    }

    private void validateFreelancer(Freelancer f) {
        if (freelancerRepository.existsByEmail(f.getEmail()))
            throw new RuntimeException("Email already in use");
        if (freelancerRepository.existsById(f.getCpf()))
            throw new RuntimeException("CPF already in use");
    }

    private void validatePassword(String pw) {
        if (pw.length() < 8) throw new RuntimeException("Password must be at least 8 characters long");
        if (!pw.matches(".*[A-Z].*")) throw new RuntimeException("Password must contain at least one uppercase letter");
        if (!pw.matches(".*\\d.*")) throw new RuntimeException("Password must contain at least one number");
    }
}

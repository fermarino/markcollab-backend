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

        FreelancerDTO dto = new FreelancerDTO();
        dto.setName(saved.getName());
        dto.setUsername(saved.getUsername());
        dto.setEmail(saved.getEmail());
        dto.setPortfolioLink(saved.getPortfolioLink());
        dto.setAboutMe(saved.getAboutMe());
        dto.setExperience(saved.getExperience());
        return dto;
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
        return freelancerRepository.findAll().stream().map(saved -> {
            FreelancerDTO dto = new FreelancerDTO();
            dto.setName(saved.getName());
            dto.setUsername(saved.getUsername());
            dto.setEmail(saved.getEmail());
            dto.setPortfolioLink(saved.getPortfolioLink());
            dto.setAboutMe(saved.getAboutMe());
            dto.setExperience(saved.getExperience());
            return dto;
        }).collect(Collectors.toList());
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

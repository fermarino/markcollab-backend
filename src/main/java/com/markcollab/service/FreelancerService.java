package com.markcollab.service;

import com.markcollab.dto.FreelancerDTO;
import com.markcollab.model.Freelancer;
import com.markcollab.repository.FreelancerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FreelancerService {

    @Autowired
    private FreelancerRepository freelancerRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public FreelancerDTO registerFreelancer(Freelancer freelancer) {
        validateFreelancer(freelancer);
        freelancer.setRole("FREELANCER");
        freelancer.setPassword(passwordEncoder.encode(freelancer.getPassword()));
        Freelancer savedFreelancer = freelancerRepository.save(freelancer);

        FreelancerDTO dto = new FreelancerDTO();
        dto.setName(savedFreelancer.getName());
        dto.setUsername(savedFreelancer.getUsername());
        dto.setEmail(savedFreelancer.getEmail());
        dto.setPortfolioLink(savedFreelancer.getPortfolioLink());
        return dto;
    }

    public Freelancer updateFreelancer(String cpf, Freelancer updatedFreelancer) {
        Freelancer freelancer = freelancerRepository.findById(cpf)
                .orElseThrow(() -> new RuntimeException("Freelancer not found"));
        freelancer.setName(updatedFreelancer.getName());
        freelancer.setUsername(updatedFreelancer.getUsername());
        freelancer.setEmail(updatedFreelancer.getEmail());
        freelancer.setPortfolioLink(updatedFreelancer.getPortfolioLink());
        return freelancerRepository.save(freelancer);
    }

    public void deleteFreelancer(String cpf) {
        Freelancer freelancer = freelancerRepository.findById(cpf)
                .orElseThrow(() -> new RuntimeException("Freelancer not found"));
        freelancerRepository.delete(freelancer);
    }

    public List<Freelancer> getAllFreelancers() {
        return freelancerRepository.findAll();
    }

    private void validateFreelancer(Freelancer freelancer) {
        if (freelancerRepository.existsByUsername(freelancer.getUsername())) {
            throw new RuntimeException("Username already in use");
        }
        if (freelancerRepository.existsByEmail(freelancer.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        if (freelancerRepository.existsById(freelancer.getCpf())) {
            throw new RuntimeException("CPF already in use");
        }
    }
}

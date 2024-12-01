package com.markcollab.service;

import com.markcollab.model.AbstractUser;
import com.markcollab.model.Employer;
import com.markcollab.model.Freelancer;
import com.markcollab.repository.EmployerRepository;
import com.markcollab.repository.FreelancerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private FreelancerRepository freelancerRepository;

    @Autowired
    private EmployerRepository employerRepository;

    public AbstractUser findUserByUsername(String username) {
        Freelancer freelancer = freelancerRepository.findByUsernameContainingIgnoreCase(username)
                .stream()
                .findFirst()
                .orElse(null);
        if (freelancer != null) {
            return freelancer;
        }
        return employerRepository.findByUsernameContainingIgnoreCase(username)
                .stream()
                .findFirst()
                .orElse(null);
    }
}

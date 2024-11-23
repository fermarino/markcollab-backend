package com.markcollab.repository;

import com.markcollab.model.Freelancer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FreelancerRepository extends JpaRepository<Freelancer, String> { // CPF é a chave primária
    List<Freelancer> findByNameContainingIgnoreCase(String name);
    List<Freelancer> findByUsernameContainingIgnoreCase(String username);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}

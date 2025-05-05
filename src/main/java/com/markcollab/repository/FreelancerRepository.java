package com.markcollab.repository;

import com.markcollab.model.Freelancer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FreelancerRepository extends JpaRepository<Freelancer, String> {
    Optional<Freelancer> findByEmail(String email);
    Optional<Freelancer> findById(String cpf); // CPF como chave primária

    boolean existsByEmail(String email);

    Optional<Freelancer> findByUsername(String username);
    boolean existsByUsername(String username);

    List<Freelancer> findByNameContainingIgnoreCase(String name);
}

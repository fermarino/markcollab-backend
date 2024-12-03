package com.markcollab.repository;

import com.markcollab.model.Employer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployerRepository extends JpaRepository<Employer, String> { // CPF é a chave primária
    List<Employer> findByNameContainingIgnoreCase(String name);
    List<Employer> findByUsernameContainingIgnoreCase(String username);

    Optional<Employer> findByUsername(String username);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}

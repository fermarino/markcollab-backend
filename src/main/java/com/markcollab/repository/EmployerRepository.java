package com.markcollab.repository;

import com.markcollab.model.Employer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployerRepository extends JpaRepository<Employer, String> {
    Optional<Employer> findByEmail(String email);
    Optional<Employer> findById(String cpf); // CPF como chave primária

    boolean existsByEmail(String email);

    Optional<Employer> findByUsername(String username);
    boolean existsByUsername(String username);

    List<Employer> findByNameContainingIgnoreCase(String name);
}

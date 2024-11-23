package com.markcollab.repository;

import com.markcollab.model.Employer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployerRepository extends JpaRepository<Employer, String> { // CPF é a chave primária
    List<Employer> findByNameContainingIgnoreCase(String name);
    List<Employer> findByUsernameContainingIgnoreCase(String username);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}

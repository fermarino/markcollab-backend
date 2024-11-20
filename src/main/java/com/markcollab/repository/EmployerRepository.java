package com.markcollab.repository;

import com.markcollab.model.Employer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployerRepository extends JpaRepository<Employer, Long> {
    List<Employer> findByNameContainingIgnoreCase(String name); // Busca parcial por nome
    List<Employer> findByUsernameContainingIgnoreCase(String username); // Busca parcial por username

    Optional<Employer> findByCpf(String cpf); // Adicione este método


}

package com.markcollab.repository;

import com.markcollab.model.Freelancer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FreelancerRepository extends JpaRepository<Freelancer, Long> {
    List<Freelancer> findByNameContainingIgnoreCase(String name); // Busca parcial por nome
    List<Freelancer> findByUsernameContainingIgnoreCase(String username); // Busca parcial por username
    Optional<Freelancer> findByCpf(String cpf); // Adicione este método

}

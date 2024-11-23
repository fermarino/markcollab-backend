package com.markcollab.repository;

import com.markcollab.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByProjectEmployerCpf(String cpf); // Projetos de um empregador

    List<Project> findByOpenTrue();
}

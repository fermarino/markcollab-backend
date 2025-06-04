// src/main/java/com/markcollab/repository/ProjectRepository.java

package com.markcollab.repository;

import com.markcollab.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    /**
     * Retorna apenas os projetos onde open = true
     */
    List<Project> findByOpenTrue();

    /**
     * Retorna TODOS os projetos (open true ou false) de um Employer pelo CPF
     */
    List<Project> findByProjectEmployer_Cpf(String cpf);

    /**
     * Retorna todos os projetos em que o Freelancer de determinado CPF foi contratado
     */
    List<Project> findByHiredFreelancer_Cpf(String freelancerCpf);
}

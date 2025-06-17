package com.markcollab.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "projects")
@Data
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;
    private String projectTitle;

    @Size(max = 5000, message = "A descrição do projeto não pode exceder 5000 caracteres.")
    @Column(length = 5000)
    private String projectDescription;

    @Size(max = 2000, message = "As especificações do projeto não podem exceder 2000 caracteres.")
    @Column(length = 2000)
    private String projectSpecifications;

    private Double projectPrice;
    private boolean open;
    private String status;

    private LocalDate deadline;

    @ManyToOne
    @JoinColumn(name = "employer_cpf")
    private Employer projectEmployer;

    @ManyToOne
    @JoinColumn(name = "hired_freelancer_cpf")
    private Freelancer hiredFreelancer;

    /**
     * Observação: a fetch type padrão para OneToMany é LAZY.
     * Graças ao @Transactional no service, não haverá LazyInitializationException.
     */
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Interest> interestedFreelancers;
}

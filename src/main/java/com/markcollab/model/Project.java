package com.markcollab.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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
    private String projectDescription;
    private String projectSpecifications;
    private Double projectPrice;
    private boolean open;
    private String status;

    // Novo campo: prazo de entrega
    private LocalDate deadline;

    @ManyToOne
    @JoinColumn(name = "employer_cpf")
    private Employer projectEmployer;

    @ManyToOne
    @JoinColumn(name = "hired_freelancer_cpf")
    private Freelancer hiredFreelancer;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Interest> interestedFreelancers;
}

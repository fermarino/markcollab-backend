package com.markcollab.model;

import jakarta.persistence.*;
import lombok.Data;


import java.util.List;

@Entity
@Table(name = "projects")
@Data

public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    @Column(nullable = false)
    private String projectTitle;

    @Column(nullable = false)
    private String projectDescription;

    @Column(nullable = false)
    private String projectSpecifications;

    @Column(nullable = false)
    private Double projectPrice;

    @Column(nullable = false)
    private boolean open = true; // Indica se o projeto está aberto para interesse

    @Column(nullable = false)
    private String status = "Aberto"; // Status do projeto: Aberto, Em andamento, Concluído

    @ManyToOne
    @JoinColumn(name = "employer_cpf", nullable = false)
    private Employer projectEmployer;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<Interest> interestedFreelancers;

    @ManyToOne
    @JoinColumn(name = "hired_freelancer_cpf")
    private Freelancer hiredFreelancer;


    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<Feedback> feedbacks;
}


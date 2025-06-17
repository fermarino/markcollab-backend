package com.markcollab.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    @Column(nullable = false, length = 150)
    private String projectTitle;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String projectDescription;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String projectSpecifications;

    @Column(nullable = false)
    private Double projectPrice;

    @Column(nullable = false)
    private LocalDate deadline;

    @Column(nullable = false)
    private boolean open;

    @Column(nullable = false)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_cpf", nullable = false)
    private Employer projectEmployer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hired_freelancer_cpf")
    private Freelancer hiredFreelancer;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Interest> interestedFreelancers;
}
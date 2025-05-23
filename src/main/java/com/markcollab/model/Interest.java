package com.markcollab.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "interests")
@Data
public class Interest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    @JsonIgnore
    private Project project;

    @ManyToOne
    @JoinColumn(name = "freelancer_cpf", nullable = false)
    @JsonIgnore
    private Freelancer freelancer;

    @Column(nullable = false)
    private String status = "Aguardando resposta";
}

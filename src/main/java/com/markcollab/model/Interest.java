package com.markcollab.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    private Project project;

    @ManyToOne
    @JoinColumn(name = "freelancer_cpf", nullable = false)
    private Freelancer freelancer;

    @Column(nullable = false)
    private String status = "Aguardando resposta";

    @Column
    private String proposalValue;

    @Column
    private String proposalDescription;

    @Column
    private String deliveryDate;
}

package com.markcollab.model;

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
    private Project project; // Projeto associado ao interesse

    @ManyToOne
    @JoinColumn(name = "freelancer_cpf", nullable = false)
    private Freelancer freelancer; // Freelancer que demonstrou interesse

    @Column(nullable = false)
    private String status = "Aguardando resposta"; // Status do interesse (Aguardando resposta, Aprovado, Recusado)
}
    
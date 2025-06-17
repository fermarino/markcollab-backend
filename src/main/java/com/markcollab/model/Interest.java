package com.markcollab.model;

import jakarta.persistence.*;
import lombok.Data;
import jakarta.validation.constraints.Size;

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


    @Size(max = 5000, message = "A descrição da proposta não pode exceder 5000 caracteres.")
    @Column(length = 5000) 
    private String proposalDescription;

    @Column
    private String deliveryDate;
}

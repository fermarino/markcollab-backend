package com.markcollab.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users_view") // Uma visão consolidada (ou lógica)
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false)
    private String role; // "FREELANCER" ou "EMPLOYER"
}

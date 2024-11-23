package com.markcollab.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "employers")
@Data
@EqualsAndHashCode(callSuper = true)
public class Employer extends AbstractUser {

    @Column(nullable = true)
    private String companyName;
}

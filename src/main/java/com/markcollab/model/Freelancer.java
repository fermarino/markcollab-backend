package com.markcollab.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "freelancers")
@Data
@EqualsAndHashCode(callSuper = true)
public class Freelancer extends AbstractUser {

    @Column(nullable = true)
    private String portfolioLink;
}

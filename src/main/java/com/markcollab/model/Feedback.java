package com.markcollab.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "feedbacks")
@Data
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackId;

    @ManyToOne
    @JoinColumn(name = "reviewer_cpf", nullable = false)
    private Employer feedbackReviewer;

    @ManyToOne
    @JoinColumn(name = "reviewed_cpf", nullable = false)
    private Freelancer feedbackReviewed;

    @Column(nullable = false)
    private Double feedbackRating;

    @Column(nullable = false, length = 1000)
    private String feedbackComment;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project; // Projeto relacionado ao feedback
}

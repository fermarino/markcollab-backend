package com.markcollab.repository;

import com.markcollab.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByFeedbackReviewedCpf(String freelancerCpf); // Feedbacks de um freelancer
    List<Feedback> findByProject_ProjectId(Long projectId); // Feedbacks de um projeto
}

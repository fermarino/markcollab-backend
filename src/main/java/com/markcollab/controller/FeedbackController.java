package com.markcollab.controller;

import com.markcollab.model.Feedback;
import com.markcollab.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    // Criar feedback
    @PostMapping("/{projectId}")
    public ResponseEntity<Feedback> createFeedback(@PathVariable Long projectId, @RequestBody Feedback feedback) {
        return ResponseEntity.ok(feedbackService.createFeedback(projectId, feedback));
    }

    // Atualizar feedback
    @PutMapping("/{feedbackId}")
    public ResponseEntity<Feedback> updateFeedback(@PathVariable Long feedbackId, @RequestBody Feedback updatedFeedback) {
        return ResponseEntity.ok(feedbackService.updateFeedback(feedbackId, updatedFeedback));
    }

    // Deletar feedback
    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long feedbackId) {
        feedbackService.deleteFeedback(feedbackId);
        return ResponseEntity.noContent().build();
    }

    // Listar feedbacks de um freelancer
    @GetMapping("/freelancer/{freelancerCpf}")
    public ResponseEntity<List<Feedback>> getFeedbacksByFreelancer(@PathVariable String freelancerCpf) {
        return ResponseEntity.ok(feedbackService.getFeedbacksByFreelancer(freelancerCpf));
    }

    // Listar feedbacks de um projeto
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Feedback>> getFeedbacksByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(feedbackService.getFeedbacksByProject(projectId));
    }
}

package com.markcollab.service;

import com.markcollab.model.Feedback;
import com.markcollab.model.Project;
import com.markcollab.repository.FeedbackRepository;
import com.markcollab.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public Feedback createFeedback(Long projectId, Feedback feedback) {
        // Buscar o projeto
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Validar se o projeto foi concluído
        if (!"Concluído".equals(project.getStatus())) {
            throw new RuntimeException("Feedback can only be given for projects marked as 'Concluído'");
        }

        // Validar se o freelancer avaliado foi contratado para o projeto
        if (project.getHiredFreelancer() == null ||
                !project.getHiredFreelancer().getCpf().equals(feedback.getFeedbackReviewed().getCpf())) {
            throw new RuntimeException("Feedback can only be given to the freelancer hired for this project");
        }

        // Validar se o contratante avaliador é o responsável pelo projeto
        if (!project.getProjectEmployer().getCpf().equals(feedback.getFeedbackReviewer().getCpf())) {
            throw new RuntimeException("Only the employer who owns the project can give feedback");
        }

        // Associar o projeto ao feedback e salvar
        feedback.setProject(project);
        return feedbackRepository.save(feedback);
    }

    // Atualizar feedback
    public Feedback updateFeedback(Long feedbackId, Feedback updatedFeedback) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));

        feedback.setFeedbackRating(updatedFeedback.getFeedbackRating());
        feedback.setFeedbackComment(updatedFeedback.getFeedbackComment());
        return feedbackRepository.save(feedback);
    }

    // Deletar feedback
    public void deleteFeedback(Long feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        feedbackRepository.delete(feedback);
    }

    // Obter feedbacks por freelancer
    public List<Feedback> getFeedbacksByFreelancer(String freelancerCpf) {
        return feedbackRepository.findByFeedbackReviewedCpf(freelancerCpf);
    }

    // Obter feedbacks por projeto
    public List<Feedback> getFeedbacksByProject(Long projectId) {
        return feedbackRepository.findByProject_ProjectId(projectId);
    }
}

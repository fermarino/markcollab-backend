package com.markcollab.controller;

import com.markcollab.dto.ProjectDTO;
import com.markcollab.model.Interest;
import com.markcollab.model.Project;
import com.markcollab.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "http://localhost:5173")
public class ProjectController {

    @Autowired private ProjectService projectService;

    @GetMapping("/open")
    public ResponseEntity<List<ProjectDTO>> getOpenProjects() {
        return ResponseEntity.ok(projectService.getOpenProjects());
    }

    @PostMapping("/{employerCpf}")
    public ResponseEntity<ProjectDTO> createProject(
            @PathVariable String employerCpf,
            @RequestBody Project project) {
        return ResponseEntity.ok(
                projectService.createProject(project, employerCpf)
        );
    }

    @PutMapping("/{projectId}/status/{employerCpf}")
    public ResponseEntity<Project> updateProjectStatus(
            @PathVariable Long projectId,
            @PathVariable String employerCpf,
            @RequestBody String newStatus) {
        return ResponseEntity.ok(
                projectService.updateProjectStatus(projectId, newStatus, employerCpf)
        );
    }

    @PostMapping("/{projectId}/hire/{freelancerCpf}/{employerCpf}")
    public ResponseEntity<ProjectDTO> hireFreelancer(
            @PathVariable Long projectId,
            @PathVariable String freelancerCpf,
            @PathVariable String employerCpf) {
        return ResponseEntity.ok(
                projectService.hireFreelancer(projectId, freelancerCpf, employerCpf)
        );
    }

    @PutMapping("/{projectId}/{employerCpf}")
    public ResponseEntity<Project> updateProject(
            @PathVariable Long projectId,
            @RequestBody Project updatedProject,
            @PathVariable String employerCpf) {
        return ResponseEntity.ok(
                projectService.updateProject(projectId, updatedProject, employerCpf)
        );
    }

    @DeleteMapping("/{projectId}/{employerCpf}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable Long projectId,
            @PathVariable String employerCpf) {
        projectService.deleteProject(projectId, employerCpf);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{projectId}/interest/{freelancerCpf}")
    public ResponseEntity<Interest> addInterest(
            @PathVariable Long projectId,
            @PathVariable String freelancerCpf) {
        return ResponseEntity.ok(
                projectService.addInterest(projectId, freelancerCpf)
        );
    }

    @PostMapping("/{projectId}/generate-description")
    public ResponseEntity<ProjectDTO> generateDescription(
            @PathVariable Long projectId) {
        return ResponseEntity.ok(
                projectService.generateProjectDescription(projectId)
        );
    }

    @GetMapping("/employer/{employerCpf}")
    public ResponseEntity<List<ProjectDTO>> getByEmployer(
            @PathVariable String employerCpf) {
        // se desejar filtrar por employer:
        // adaptar service para retornar List<ProjectDTO>
        return ResponseEntity.ok(
                projectService.getOpenProjects() // ou outro método
        );
    }
}

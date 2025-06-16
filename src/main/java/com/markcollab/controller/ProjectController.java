package com.markcollab.controller;

import com.markcollab.dto.ProjectDTO;
import com.markcollab.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "https://markcollab-backend.onrender.com")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    /**
     * 1) Retorna todos os projetos abertos (open=true).
     *    GET /api/projects/open
     */
    @GetMapping("/open")
    public ResponseEntity<List<ProjectDTO>> getOpenProjects() {
        return ResponseEntity.ok(projectService.getOpenProjects());
    }

    /**
     * 2) Retorna um único projeto no formato DTO.
     *    GET /api/projects/{projectId}
     */
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long projectId) {
        try {
            ProjectDTO dto = projectService.getProjectById(projectId);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 3) Cria novo projeto (POST /api/projects/{employerCpf})
     */
    @PostMapping("/{employerCpf}")
    public ResponseEntity<ProjectDTO> createProject(
            @PathVariable String employerCpf,
            @RequestBody com.markcollab.model.Project project) {
        ProjectDTO saved = projectService.createProject(project, employerCpf);
        return ResponseEntity.ok(saved);
    }

    /**
     * 4) Atualiza apenas o status de um projeto (PUT /api/projects/{projectId}/status/{employerCpf})
     */
    @PutMapping("/{projectId}/status/{employerCpf}")
    public ResponseEntity<?> updateProjectStatus(
            @PathVariable Long projectId,
            @PathVariable String employerCpf,
            @RequestBody String newStatus) {
        try {
            com.markcollab.model.Project updated = projectService.updateProjectStatus(projectId, newStatus, employerCpf);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("Unauthorized action".equals(msg)) {
                return ResponseEntity.status(403).body("Unauthorized action");
            }
            return ResponseEntity.badRequest().body(msg);
        }
    }

    /**
     * 5) Contrata um freelancer para este projeto:
     *    POST /api/projects/{projectId}/hire/{freelancerCpf}/{employerCpf}
     */
    @PostMapping("/{projectId}/hire/{freelancerCpf}/{employerCpf}")
    public ResponseEntity<ProjectDTO> hireFreelancer(
            @PathVariable Long projectId,
            @PathVariable String freelancerCpf,
            @PathVariable String employerCpf) {
        ProjectDTO dto = projectService.hireFreelancer(projectId, freelancerCpf, employerCpf);
        return ResponseEntity.ok(dto);
    }

    /**
     * 6) Atualiza dados gerais do projeto (PUT /api/projects/{projectId}/{employerCpf})
     */
    @PutMapping("/{projectId}/{employerCpf}")
    public ResponseEntity<?> updateProject(
            @PathVariable Long projectId,
            @RequestBody com.markcollab.model.Project updatedProject,
            @PathVariable String employerCpf) {
        try {
            com.markcollab.model.Project updated = projectService.updateProject(projectId, updatedProject, employerCpf);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("Unauthorized action".equals(msg)) {
                return ResponseEntity.status(403).body("Unauthorized action");
            }
            return ResponseEntity.badRequest().body(msg);
        }
    }

    /**
     * 7) Deleta projeto (DELETE /api/projects/{projectId}/{employerCpf})
     */
    @DeleteMapping("/{projectId}/{employerCpf}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable Long projectId,
            @PathVariable String employerCpf) {
        projectService.deleteProject(projectId, employerCpf);
        return ResponseEntity.noContent().build();
    }

    /**
     * 8) Cria Interesse (proposta) para este projeto:
     *    POST /api/projects/{projectId}/interest/{freelancerCpf}
     */
    @PostMapping("/{projectId}/interest/{freelancerCpf}")
    public ResponseEntity<?> addInterest(
            @PathVariable Long projectId,
            @PathVariable String freelancerCpf) {
        return ResponseEntity.ok(projectService.addInterest(projectId, freelancerCpf));
    }

    /**
     * 9) Gera descrição automática via IA (POST /api/projects/{projectId}/generate-description)
     */
    @PostMapping("/{projectId}/generate-description")
    public ResponseEntity<ProjectDTO> generateDescription(
            @PathVariable Long projectId) {
        ProjectDTO dto = projectService.generateProjectDescription(projectId);
        return ResponseEntity.ok(dto);
    }

    /**
     * 10) Retorna todos os projetos (abertos ou não) de um empregador específico.
     *     GET /api/projects/employer/{employerCpf}
     */
    @GetMapping("/employer/{employerCpf}")
    public ResponseEntity<List<ProjectDTO>> getByEmployer(@PathVariable String employerCpf) {
        try {
            List<ProjectDTO> lista = projectService.getProjectsByEmployer(employerCpf);
            return ResponseEntity.ok(lista);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 11) Retorna todos os projetos em que um freelancer foi contratado.
     *     GET /api/projects/freelancer/{freelancerCpf}
     */
    @GetMapping("/freelancer/{freelancerCpf}")
    public ResponseEntity<List<ProjectDTO>> getByFreelancer(@PathVariable String freelancerCpf) {
        try {
            List<ProjectDTO> lista = projectService.getProjectsByFreelancer(freelancerCpf);
            return ResponseEntity.ok(lista);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}

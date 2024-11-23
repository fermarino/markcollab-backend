package com.markcollab.controller;

import com.markcollab.model.Employer;
import com.markcollab.model.Freelancer;
import com.markcollab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Criar Freelancer
    @PostMapping("/freelancers")
    public ResponseEntity<?> registerFreelancer(@RequestBody Freelancer freelancer) {
        try {
            Freelancer savedFreelancer = userService.registerFreelancer(freelancer);
            return ResponseEntity.ok(savedFreelancer);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Criar Employer
    @PostMapping("/employers")
    public ResponseEntity<?> registerEmployer(@RequestBody Employer employer) {
        try {
            Employer savedEmployer = userService.registerEmployer(employer);
            return ResponseEntity.ok(savedEmployer);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Atualizar Freelancer
    @PutMapping("/freelancers/{cpf}")
    public ResponseEntity<?> updateFreelancer(@PathVariable String cpf, @RequestBody Freelancer freelancer) {
        try {
            Freelancer updatedFreelancer = userService.updateFreelancer(cpf, freelancer);
            return ResponseEntity.ok(updatedFreelancer);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Atualizar Employer
    @PutMapping("/employers/{cpf}")
    public ResponseEntity<?> updateEmployer(@PathVariable String cpf, @RequestBody Employer employer) {
        try {
            Employer updatedEmployer = userService.updateEmployer(cpf, employer);
            return ResponseEntity.ok(updatedEmployer);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Deletar Freelancer
    @DeleteMapping("/freelancers/{cpf}")
    public ResponseEntity<?> deleteFreelancer(@PathVariable String cpf) {
        try {
            userService.deleteFreelancer(cpf);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Deletar Employer
    @DeleteMapping("/employers/{cpf}")
    public ResponseEntity<?> deleteEmployer(@PathVariable String cpf) {
        try {
            userService.deleteEmployer(cpf);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Obter todos os Freelancers
    @GetMapping("/freelancers")
    public ResponseEntity<List<Freelancer>> getAllFreelancers() {
        return ResponseEntity.ok(userService.getAllFreelancers());
    }

    // Obter todos os Employers
    @GetMapping("/employers")
    public ResponseEntity<List<Employer>> getAllEmployers() {
        return ResponseEntity.ok(userService.getAllEmployers());
    }
}

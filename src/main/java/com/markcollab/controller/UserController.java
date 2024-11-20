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
    public ResponseEntity<Freelancer> registerFreelancer(@RequestBody Freelancer freelancer) {
        Freelancer savedFreelancer = userService.registerFreelancer(freelancer);
        return ResponseEntity.ok(savedFreelancer);
    }

    // Criar Employer
    @PostMapping("/employers")
    public ResponseEntity<Employer> registerEmployer(@RequestBody Employer employer) {
        Employer savedEmployer = userService.registerEmployer(employer);
        return ResponseEntity.ok(savedEmployer);
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

    // Buscar Freelancers por Nome
    @GetMapping("/freelancers/name/{name}")
    public ResponseEntity<List<Freelancer>> findFreelancersByName(@PathVariable String name) {
        return ResponseEntity.ok(userService.findFreelancersByName(name));
    }

    // Buscar Freelancers por Username
    @GetMapping("/freelancers/username/{username}")
    public ResponseEntity<List<Freelancer>> findFreelancersByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.findFreelancersByUsername(username));
    }

    // Buscar Employers por Nome
    @GetMapping("/employers/name/{name}")
    public ResponseEntity<List<Employer>> findEmployersByName(@PathVariable String name) {
        return ResponseEntity.ok(userService.findEmployersByName(name));
    }

    // Buscar Employers por Username
    @GetMapping("/employers/username/{username}")
    public ResponseEntity<List<Employer>> findEmployersByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.findEmployersByUsername(username));
    }
}

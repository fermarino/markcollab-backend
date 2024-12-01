package com.markcollab.controller;

import com.markcollab.dto.FreelancerDTO;
import com.markcollab.model.Freelancer;
import com.markcollab.service.FreelancerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/freelancers")
public class FreelancerController {

    @Autowired
    private FreelancerService freelancerService;

    @PostMapping
    public ResponseEntity<FreelancerDTO> registerFreelancer(@RequestBody Freelancer freelancer) {
        return ResponseEntity.ok(freelancerService.registerFreelancer(freelancer));
    }

    @PutMapping("/{cpf}")
    public ResponseEntity<Freelancer> updateFreelancer(@PathVariable String cpf, @RequestBody Freelancer freelancer) {
        return ResponseEntity.ok(freelancerService.updateFreelancer(cpf, freelancer));
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> deleteFreelancer(@PathVariable String cpf) {
        freelancerService.deleteFreelancer(cpf);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Freelancer>> getAllFreelancers() {
        return ResponseEntity.ok(freelancerService.getAllFreelancers());
    }
}

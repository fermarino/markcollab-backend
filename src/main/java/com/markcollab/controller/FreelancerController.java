package com.markcollab.controller;

import com.markcollab.dto.FreelancerDTO;
import com.markcollab.model.Freelancer;
import com.markcollab.service.FreelancerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/freelancers")
@CrossOrigin(origins = "http://localhost:5173")
public class FreelancerController {

    @Autowired
    private FreelancerService service;

    @PostMapping
    public ResponseEntity<FreelancerDTO> create(@Valid @RequestBody Freelancer f) {
        return ResponseEntity.ok(service.registerFreelancer(f));
    }

    @PutMapping("/{cpf}")
    public ResponseEntity<Freelancer> update(@PathVariable String cpf,
                                             @Valid @RequestBody Freelancer f) {
        return ResponseEntity.ok(service.updateFreelancer(cpf, f));
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> delete(@PathVariable String cpf) {
        service.deleteFreelancer(cpf);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<FreelancerDTO>> list() {
        return ResponseEntity.ok(service.getAllFreelancers());
    }

    /**
     * 5) Busca um Freelancer por CPF (para exibir perfil).
     *    GET /api/freelancers/{cpf}
     */
    @GetMapping("/{cpf}")
    public ResponseEntity<FreelancerDTO> findByCpf(@PathVariable String cpf) {
        FreelancerDTO dto = service.getFreelancerByCpf(cpf);
        return ResponseEntity.ok(dto);
    }
}

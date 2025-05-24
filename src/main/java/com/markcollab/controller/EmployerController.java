package com.markcollab.controller;

import com.markcollab.dto.EmployerDTO;
import com.markcollab.model.Employer;
import com.markcollab.service.EmployerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employers")
public class EmployerController {

    @Autowired private EmployerService service;

    @PostMapping
    public ResponseEntity<EmployerDTO> create(@Valid @RequestBody Employer e) {
        return ResponseEntity.ok(service.registerEmployer(e));
    }

    @PutMapping("/{cpf}")
    public ResponseEntity<Employer> update(@PathVariable String cpf,
                                           @Valid @RequestBody Employer e) {
        return ResponseEntity.ok(service.updateEmployer(cpf, e));
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> delete(@PathVariable String cpf) {
        service.deleteEmployer(cpf);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<EmployerDTO>> list() {
        return ResponseEntity.ok(service.getAllEmployers());
    }
}

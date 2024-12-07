package com.markcollab.controller;

import com.markcollab.dto.EmployerDTO;
import com.markcollab.model.Employer;
import com.markcollab.service.EmployerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employers")
public class EmployerController {

    @Autowired
    private EmployerService employerService;

    @PostMapping
    public ResponseEntity<EmployerDTO> registerEmployer(@RequestBody Employer employer) {
        return ResponseEntity.ok(employerService.registerEmployer(employer));
    }

    @PutMapping("/{cpf}")
    public ResponseEntity<Employer> updateEmployer(@PathVariable String cpf, @RequestBody Employer employer) {
        return ResponseEntity.ok(employerService.updateEmployer(cpf, employer));
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> deleteEmployer(@PathVariable String cpf) {
        employerService.deleteEmployer(cpf);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<EmployerDTO>> getAllEmployers() {
        return ResponseEntity.ok(employerService.getAllEmployers());
    }

}

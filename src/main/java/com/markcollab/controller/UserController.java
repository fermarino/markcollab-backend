package com.markcollab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.markcollab.exception.UnauthorizedException;
import com.markcollab.model.Employer;
import com.markcollab.model.Freelancer;
import com.markcollab.repository.EmployerRepository;
import com.markcollab.repository.FreelancerRepository;
import com.markcollab.service.CloudinaryService;
import com.markcollab.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired private FreelancerRepository freelancerRepo;
    @Autowired private EmployerRepository employerRepo;
    @Autowired private JwtService jwtService;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private CloudinaryService cloudinaryService;

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpServletRequest req) {
        String cpf = extractCpf(req);
        return freelancerRepo.findById(cpf)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .or(() -> employerRepo.findById(cpf).map(ResponseEntity::ok))
                .orElseThrow(() -> new UnauthorizedException("Usuário não encontrado."));
    }

    @PutMapping("/me/update")
    public ResponseEntity<?> updateMe(HttpServletRequest req, @RequestBody Map<String, Object> data) {
        String cpf = extractCpf(req);

        if (freelancerRepo.existsById(cpf)) {
            Freelancer userToUpdate = freelancerRepo.findById(cpf)
                    .orElseThrow(() -> new UnauthorizedException("Freelancer não encontrado."));

            if (data.containsKey("name")) {
                userToUpdate.setName((String) data.get("name"));
            }
            if (data.containsKey("aboutMe")) {
                userToUpdate.setAboutMe((String) data.get("aboutMe"));
            }
            if (data.containsKey("experience")) {
                userToUpdate.setExperience((String) data.get("experience"));
            }

            Freelancer updatedUser = freelancerRepo.save(userToUpdate);
            return ResponseEntity.ok(updatedUser);
        }

        if (employerRepo.existsById(cpf)) {
            Employer userToUpdate = employerRepo.findById(cpf)
                    .orElseThrow(() -> new UnauthorizedException("Employer não encontrado."));

            if (data.containsKey("name")) {
                userToUpdate.setName((String) data.get("name"));
            }
            if (data.containsKey("aboutMe")) {
                userToUpdate.setAboutMe((String) data.get("aboutMe"));
            }
            if (data.containsKey("companyName")) {
                userToUpdate.setCompanyName((String) data.get("companyName"));
            }

            Employer updatedUser = employerRepo.save(userToUpdate);
            return ResponseEntity.ok(updatedUser);
        }

        throw new UnauthorizedException("Usuário não encontrado para atualização.");
    }

    @PostMapping("/upload-profile-picture")
    public ResponseEntity<?> uploadProfilePicture(HttpServletRequest req, @RequestParam("profilePicture") MultipartFile file) {
        String cpf = extractCpf(req);
        String publicUrl;

        try {
            publicUrl = cloudinaryService.upload(file);
        } catch (Exception e) {
            System.out.println("### ERRO NO UPLOAD DO CLOUDINARY ###");
            e.printStackTrace();
            System.out.println("#####################################");
            return ResponseEntity
                    .status(500)
                    .body(Map.of("message", "Falha no serviço de upload. Verifique os logs do servidor."));
        }

        if (freelancerRepo.existsById(cpf)) {
            Freelancer user = freelancerRepo.findById(cpf).get();
            user.setProfilePicture(publicUrl);
            freelancerRepo.save(user);
        } else if (employerRepo.existsById(cpf)) {
            Employer user = employerRepo.findById(cpf).get();
            user.setProfilePicture(publicUrl);
            employerRepo.save(user);
        } else {
            throw new UnauthorizedException("Usuário não encontrado.");
        }

        return ResponseEntity.ok(Map.of("message", "Foto de perfil atualizada com sucesso!", "profilePictureUrl", publicUrl));
    }

    private String extractCpf(HttpServletRequest req) {
        String h = req.getHeader("Authorization");
        if (h==null||!h.startsWith("Bearer ")) throw new UnauthorizedException("Token inválido.");
        return jwtService.extractCpf(h.substring(7));
    }
}
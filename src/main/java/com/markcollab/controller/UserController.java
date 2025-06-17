package com.markcollab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.markcollab.exception.UnauthorizedException;
import com.markcollab.model.Employer;
import com.markcollab.model.Freelancer;
import com.markcollab.repository.EmployerRepository;
import com.markcollab.repository.FreelancerRepository;
import com.markcollab.service.CloudinaryService; // IMPORTADO
import com.markcollab.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // IMPORTADO

import java.io.IOException; // IMPORTADO
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired private FreelancerRepository freelancerRepo;
    @Autowired private EmployerRepository employerRepo;
    @Autowired private JwtService jwtService;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private CloudinaryService cloudinaryService; // INJETADO

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

        // Lógica para Freelancer
        if (freelancerRepo.existsById(cpf)) {
            Freelancer userToUpdate = freelancerRepo.findById(cpf)
                    .orElseThrow(() -> new UnauthorizedException("Freelancer não encontrado."));

            // Atualiza apenas os campos que vieram na requisição
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

        // Lógica para Employer
        if (employerRepo.existsById(cpf)) {
            Employer userToUpdate = employerRepo.findById(cpf)
                    .orElseThrow(() -> new UnauthorizedException("Employer não encontrado."));

            // Atualiza apenas os campos que vieram na requisição
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

    // === MÉTODO DE UPLOAD ATUALIZADO ===
    @PostMapping("/upload-profile-picture")
    public ResponseEntity<?> uploadProfilePicture(HttpServletRequest req, @RequestParam("profilePicture") MultipartFile file) {
        String cpf = extractCpf(req);
        String publicUrl;

        try {
            // 1. Faz o upload do arquivo para o Cloudinary e obtém a URL segura.
            publicUrl = cloudinaryService.upload(file);
        } catch (IOException e) {
            // Em caso de erro no upload, retorna uma resposta de erro para o frontend.
            return ResponseEntity.internalServerError().body(Map.of("message", "Erro ao fazer upload da imagem."));
        }

        // 2. Salva a URL no perfil do usuário (Freelancer ou Employer).
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

        // 3. Retorna uma resposta de sucesso com a nova URL da imagem.
        return ResponseEntity.ok(Map.of("message", "Foto de perfil atualizada com sucesso!", "profilePictureUrl", publicUrl));
    }


    private String extractCpf(HttpServletRequest req) {
        String h = req.getHeader("Authorization");
        if (h==null||!h.startsWith("Bearer ")) throw new UnauthorizedException("Token inválido.");
        return jwtService.extractCpf(h.substring(7));
    }
}
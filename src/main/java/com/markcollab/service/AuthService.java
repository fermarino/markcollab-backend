package com.markcollab.service;

import com.markcollab.exception.BadRequestException;
import com.markcollab.exception.UnauthorizedException;
import com.markcollab.model.AbstractUser;
import com.markcollab.model.Employer;
import com.markcollab.model.Freelancer;
import com.markcollab.payload.AuthRegisterRequest;
import com.markcollab.repository.EmployerRepository;
import com.markcollab.repository.FreelancerRepository;
import com.markcollab.validation.PasswordValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class AuthService {

    private final EmployerRepository employerRepo;
    private final FreelancerRepository freelancerRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final PasswordValidator pwdValidator;

    public void register(AuthRegisterRequest req) {
        if (employerRepo.existsById(req.getCpf()) || freelancerRepo.existsById(req.getCpf()))
            throw new BadRequestException("cpf: CPF já cadastrado.");
        if (employerRepo.existsByEmail(req.getEmail()) || freelancerRepo.existsByEmail(req.getEmail()))
            throw new BadRequestException("email: Email já em uso.");
        if (employerRepo.existsByUsername(req.getUsername()) || freelancerRepo.existsByUsername(req.getUsername()))
            throw new BadRequestException("username: Username já em uso.");

        pwdValidator.validate(req.getPassword());

        if ("EMPLOYER".equals(req.getRole())) {
            Employer e = new Employer();
            e.setCpf(req.getCpf());
            e.setName(req.getName());
            e.setUsername(req.getUsername());
            e.setEmail(req.getEmail());
            e.setPassword(encoder.encode(req.getPassword()));
            e.setRole("EMPLOYER");
            e.setCompanyName(req.getCompanyName());
            e.setAboutMe(req.getAboutMe());
            employerRepo.save(e);

        } else {
            Freelancer f = new Freelancer();
            f.setCpf(req.getCpf());
            f.setName(req.getName());
            f.setUsername(req.getUsername());
            f.setEmail(req.getEmail());
            f.setPassword(encoder.encode(req.getPassword()));
            f.setRole("FREELANCER");
            f.setPortfolioLink(req.getPortfolioLink());
            f.setAboutMe(req.getAboutMe());
            f.setExperience(req.getExperience());
            freelancerRepo.save(f);
        }
    }

    public String authenticate(String id, String pw) {
        var opt = employerRepo.findByEmail(id).map(u->(AbstractUser)u)
                .or(() -> employerRepo.findById(id).map(u->(AbstractUser)u))
                .or(() -> employerRepo.findByUsername(id).map(u->(AbstractUser)u))
                .or(() -> freelancerRepo.findByEmail(id).map(u->(AbstractUser)u))
                .or(() -> freelancerRepo.findById(id).map(u->(AbstractUser)u))
                .or(() -> freelancerRepo.findByUsername(id).map(u->(AbstractUser)u));

        var user = opt.orElseThrow(() -> new UnauthorizedException("Credenciais inválidas."));
        if (!encoder.matches(pw, user.getPassword()))
            throw new UnauthorizedException("Credenciais inválidas.");

        return jwtService.generateToken(user);
    }
}

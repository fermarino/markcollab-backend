package com.markcollab.validation;

import com.markcollab.exception.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class PasswordValidator {
    public void validate(String pw) {
        if (pw == null || pw.length() < 8)
            throw new BadRequestException("password: Senha deve ter no mínimo 8 caracteres");
        if (!pw.matches(".*[A-Z].*"))
            throw new BadRequestException("password: Senha deve conter ao menos uma letra maiúscula");
        if (!pw.matches(".*\\d.*"))
            throw new BadRequestException("password: Senha deve conter ao menos um número");
    }
}

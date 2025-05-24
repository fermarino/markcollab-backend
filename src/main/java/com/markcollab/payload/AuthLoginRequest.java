package com.markcollab.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthLoginRequest {
    @NotBlank private String identifier;
    @NotBlank private String password;
}

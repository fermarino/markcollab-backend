// src/main/java/com/markcollab/payload/AuthRegisterRequest.java
package com.markcollab.payload;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AuthRegisterRequest {
    @NotBlank @Pattern(regexp="\\d{11}")                private String cpf;
    @NotBlank                                          private String name;
    @NotBlank @Size(min=3,max=20)                      private String username;
    @NotBlank @Email                                   private String email;
    @NotBlank @Size(min=8, message="Senha muito curta")private String password;
    @NotBlank @Pattern(regexp="EMPLOYER|FREELANCER")    private String role;
    private String companyName;
    private String portfolioLink;
    private String aboutMe;
    private String experience;
}

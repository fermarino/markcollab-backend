// src/main/java/com/markcollab/dto/EmployerDTO.java
package com.markcollab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployerDTO {
    private String name;
    private String username;
    private String email;
    private String companyName;
    private String aboutMe;
}

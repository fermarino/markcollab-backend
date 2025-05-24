// src/main/java/com/markcollab/dto/FreelancerDTO.java
package com.markcollab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FreelancerDTO {
    private String name;
    private String username;
    private String email;
    private String portfolioLink;
    private String aboutMe;
    private String experience;
}

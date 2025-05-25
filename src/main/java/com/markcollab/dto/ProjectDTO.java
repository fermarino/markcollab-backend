package com.markcollab.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ProjectDTO {
    private Long projectId;
    private String projectTitle;
    private String projectDescription;
    private String projectSpecifications;
    private Double projectPrice;
    private boolean open;
    private String status;
    private LocalDate deadline;          
    private EmployerDTO projectEmployer;
    private FreelancerDTO hiredFreelancer;
}

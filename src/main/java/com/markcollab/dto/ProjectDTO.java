package com.markcollab.dto;

import lombok.Data;

@Data
public class ProjectDTO {
    private Long projectId;
    private String projectTitle;
    private String projectDescription;
    private String projectSpecifications;
    private Double projectPrice;
    private boolean open;
    private String status;
    private EmployerDTO projectEmployer;
    private FreelancerDTO hiredFreelancer;
}
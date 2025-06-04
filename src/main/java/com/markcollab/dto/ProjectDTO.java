package com.markcollab.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO que representa como expor um Projeto via JSON.
 * O campo 'deadline' é LocalDate, mas o Jackson (com WRITE_DATES_AS_TIMESTAMPS=false
 * e jackson-datatype-jsr310 no classpath) irá serializá-lo como "yyyy-MM-dd".
 */
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

    /**
     * Garante que, ao serializar para JSON, o campo deadline apareça como string "yyyy-MM-dd".
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate deadline;

    private EmployerDTO projectEmployer;
    private FreelancerDTO hiredFreelancer;
}

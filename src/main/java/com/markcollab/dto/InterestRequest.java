package com.markcollab.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Usado pelo método POST /api/interests/ para criar uma nova proposta.
 */
@Data
public class InterestRequest {
    private Long projectId;
    private String freelancerCpf;
    private String proposalValue;

    @Size(max = 5000, message = "A descrição da proposta não pode exceder 5000 caracteres.")
    private String proposalDescription;

    private String deliveryDate;
}

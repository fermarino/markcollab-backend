package com.markcollab.dto;

import lombok.Data;

/**
 * Usado pelo método POST /api/interests/ para criar uma nova proposta.
 */
@Data
public class InterestRequest {
    private Long projectId;
    private String freelancerCpf;
    private String proposalValue;
    private String proposalDescription;
    private String deliveryDate;
}

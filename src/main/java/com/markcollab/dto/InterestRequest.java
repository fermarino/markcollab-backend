package com.markcollab.dto;

import lombok.Data;

@Data
public class InterestRequest {
    private Long projectId;
    private String freelancerCpf;
    private String proposalValue;
    private String proposalDescription;
    private String deliveryDate;
}

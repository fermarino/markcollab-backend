package com.markcollab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InterestResponseDTO {
    private Long id;
    private String status;
    private String proposalValue;
    private String proposalDescription;
    private String deliveryDate;
}

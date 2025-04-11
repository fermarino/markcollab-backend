package com.markcollab.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectIARequestDTO {

    @JsonProperty("titulo")
    private String projectTitle;

    @JsonProperty("especificacoes")
    private String projectSpecifications;

    @JsonProperty("prazo")
    private String projectDeadline;
}

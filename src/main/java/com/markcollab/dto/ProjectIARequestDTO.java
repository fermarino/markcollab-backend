package com.markcollab.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectIARequestDTO {

    @JsonProperty("name")
    private String projectTitle;

    @JsonProperty("specifications")
    private String projectSpecifications;

    @JsonProperty("deadline")
    private String projectDeadline;
}
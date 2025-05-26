package com.markcollab.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EmailRequestDTO {

    @JsonProperty("freelancer")
    private ContactInfo freelancer;

    @JsonProperty("employer")
    private ContactInfo employer;

    @JsonProperty("project")
    private ProjectInfo project;

    @Data
    public static class ContactInfo {
        private String name;
        private String email;
    }

    @Data
    public static class ProjectInfo {
        private String title;
        private String description;
    }
}
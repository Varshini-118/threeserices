package com.learnvault.instructorsessionmanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstructorRequest {
    private Integer userId;
    private String specializations;
    private String qualificationLevel;
}
package com.learnvault.instructorsessionmanagement.dto.response;

import com.learnvault.instructorsessionmanagement.entity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstructorResponse {

    private String instructorName;   // Add this

    private Integer instructorId;
    private Integer userId;
    private String specializations;
    private String qualificationLevel;
    private Double ratingAvg;
    private Status status;
}
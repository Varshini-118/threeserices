package com.learnvault.assessmentevaluation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttemptRequest {
    private Integer assessmentId;
    private Integer learnerId;
    private Integer score;
    private Integer timeTakenMinutes;
}
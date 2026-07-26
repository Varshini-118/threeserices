package com.learnvault.instructorsessionmanagement.dto.response;

import com.learnvault.instructorsessionmanagement.entity.enums.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingSessionResponse {
    private Integer sessionId;
    private Integer courseId;
    private Integer instructorId;
    private LocalDate sessionDate;
    private Time startTime;
    private Time endTime;
    private String venue;
    private Integer maxCapacity;
    private SessionStatus status;
}
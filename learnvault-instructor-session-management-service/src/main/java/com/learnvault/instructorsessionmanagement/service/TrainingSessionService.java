package com.learnvault.instructorsessionmanagement.service;

import com.learnvault.instructorsessionmanagement.dto.request.TrainingSessionRequest;
import com.learnvault.instructorsessionmanagement.dto.response.CapacityResponse;
import com.learnvault.instructorsessionmanagement.dto.response.TrainingSessionResponse;

import java.util.List;

public interface TrainingSessionService {
    TrainingSessionResponse scheduleSession(TrainingSessionRequest request);
    TrainingSessionResponse getSessionById(Integer id);
    List<TrainingSessionResponse> getAllSessions(Integer courseId, Integer instructorId);
    TrainingSessionResponse cancelSession(Integer id);
    CapacityResponse getCapacity(Integer id);
}
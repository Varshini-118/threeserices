package com.learnvault.instructorsessionmanagement.service.impl;

import com.learnvault.instructorsessionmanagement.dto.request.SessionRegistrationRequest;
import com.learnvault.instructorsessionmanagement.dto.response.SessionRegistrationResponse;
import com.learnvault.instructorsessionmanagement.entity.SessionRegistration;
import com.learnvault.instructorsessionmanagement.entity.TrainingSession;
import com.learnvault.instructorsessionmanagement.entity.enums.AttendanceStatus;
import com.learnvault.instructorsessionmanagement.exception.BadRequestException;
import com.learnvault.instructorsessionmanagement.exception.ResourceNotFoundException;
import com.learnvault.instructorsessionmanagement.repository.SessionRegistrationRepository;
import com.learnvault.instructorsessionmanagement.repository.TrainingSessionRepository;
import com.learnvault.instructorsessionmanagement.service.SessionRegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionRegistrationServiceImpl implements SessionRegistrationService {

    private final SessionRegistrationRepository sessionRegistrationRepository;
    private final TrainingSessionRepository trainingSessionRepository;

    @Override
    public SessionRegistrationResponse registerLearner(SessionRegistrationRequest request) {
        log.info("Registering learner {} for session {}", request.getLearnerId(), request.getSessionId());

        TrainingSession session = trainingSessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with id: " + request.getSessionId()));

        long registeredCount = sessionRegistrationRepository.countBySession_SessionId(request.getSessionId());
        if (registeredCount >= session.getMaxCapacity()) {
            throw new BadRequestException("Session is full. Max capacity: " + session.getMaxCapacity());
        }

        SessionRegistration registration = SessionRegistration.builder()
                .session(session)
                .learnerId(request.getLearnerId())
                .attendanceStatus(AttendanceStatus.REGISTERED)
                .build();

        SessionRegistration saved = sessionRegistrationRepository.save(registration);
        return mapToResponse(saved);
    }

    @Override
    public List<SessionRegistrationResponse> getRegistrationsBySession(Integer sessionId) {
        log.info("Fetching registrations for session: {}", sessionId);
        return sessionRegistrationRepository.findBySession_SessionId(sessionId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SessionRegistrationResponse markAttendance(Integer id, String status) {
        log.info("Marking attendance {} for registration {}", status, id);
        SessionRegistration registration = sessionRegistrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found with id: " + id));
        registration.setAttendanceStatus(AttendanceStatus.valueOf(status));
        SessionRegistration updated = sessionRegistrationRepository.save(registration);
        return mapToResponse(updated);
    }

    private SessionRegistrationResponse mapToResponse(SessionRegistration registration) {
        return SessionRegistrationResponse.builder()
                .registrationId(registration.getRegistrationId())
                .sessionId(registration.getSession().getSessionId())
                .learnerId(registration.getLearnerId())
                .registeredDate(registration.getRegisteredDate())
                .attendanceStatus(registration.getAttendanceStatus())
                .build();
    }
}
package com.learnvault.instructorsessionmanagement.service.impl;

import com.learnvault.instructorsessionmanagement.dto.request.TrainingSessionRequest;
import com.learnvault.instructorsessionmanagement.dto.response.CapacityResponse;
import com.learnvault.instructorsessionmanagement.dto.response.TrainingSessionResponse;
import com.learnvault.instructorsessionmanagement.entity.Instructor;
import com.learnvault.instructorsessionmanagement.entity.TrainingSession;
import com.learnvault.instructorsessionmanagement.entity.enums.SessionStatus;
import com.learnvault.instructorsessionmanagement.exception.BadRequestException;
import com.learnvault.instructorsessionmanagement.exception.ResourceNotFoundException;
import com.learnvault.instructorsessionmanagement.repository.InstructorRepository;
import com.learnvault.instructorsessionmanagement.repository.SessionRegistrationRepository;
import com.learnvault.instructorsessionmanagement.repository.TrainingSessionRepository;
import com.learnvault.instructorsessionmanagement.service.TrainingSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingSessionServiceImpl implements TrainingSessionService {

    private final TrainingSessionRepository trainingSessionRepository;
    private final InstructorRepository instructorRepository;
    private final SessionRegistrationRepository sessionRegistrationRepository;

    @Override
    @Transactional
    public TrainingSessionResponse scheduleSession(TrainingSessionRequest request) {
        log.info("Scheduling session for course: {}", request.getCourseId());

        Instructor instructor = instructorRepository.findById(request.getInstructorId())
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + request.getInstructorId()));

        TrainingSession session = TrainingSession.builder()
                .courseId(request.getCourseId())
                .instructor(instructor)
                .sessionDate(request.getSessionDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .venue(request.getVenue())
                .maxCapacity(request.getMaxCapacity())
                .status(SessionStatus.SCHEDULED)
                .build();

        TrainingSession saved = trainingSessionRepository.save(session);
        return mapToResponse(saved);
    }

    @Override
    public TrainingSessionResponse getSessionById(Integer id) {
        log.info("Fetching session by ID: {}", id);
        TrainingSession session = trainingSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Training session not found with id: " + id));
        return mapToResponse(session);
    }

    @Override
    public List<TrainingSessionResponse> getAllSessions(Integer courseId, Integer instructorId) {
        log.info("Fetching sessions - courseId: {}, instructorId: {}", courseId, instructorId);
        List<TrainingSession> sessions;
        if (courseId != null) {
            sessions = trainingSessionRepository.findByCourseId(courseId);
        } else if (instructorId != null) {
            sessions = trainingSessionRepository.findByInstructor_InstructorId(instructorId);
        } else {
            sessions = trainingSessionRepository.findAll();
        }
        return sessions.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public TrainingSessionResponse cancelSession(Integer id) {
        log.info("Cancelling session: {}", id);
        TrainingSession session = trainingSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Training session not found with id: " + id));
        
        if (session.getStatus() != SessionStatus.SCHEDULED) {
            throw new BadRequestException("Only SCHEDULED sessions can be cancelled");
        }
        
        session.setStatus(SessionStatus.CANCELLED);
        TrainingSession updated = trainingSessionRepository.save(session);
        return mapToResponse(updated);
    }

    @Override
    public CapacityResponse getCapacity(Integer id) {
        TrainingSession session = trainingSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Training session not found with id: " + id));
        long registered = sessionRegistrationRepository.countBySession_SessionId(id);
        return CapacityResponse.builder()
                .maxCapacity(session.getMaxCapacity())
                .registered(registered)
                .available(session.getMaxCapacity() - (int) registered)
                .build();
    }

    private TrainingSessionResponse mapToResponse(TrainingSession session) {
        return TrainingSessionResponse.builder()
                .sessionId(session.getSessionId())
                .courseId(session.getCourseId())
                .instructorId(session.getInstructor().getInstructorId())
                .sessionDate(session.getSessionDate())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .venue(session.getVenue())
                .maxCapacity(session.getMaxCapacity())
                .status(session.getStatus())
                .build();
    }
}
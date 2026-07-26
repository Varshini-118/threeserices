package com.learnvault.assessmentevaluation.service.impl;

import com.learnvault.assessmentevaluation.dto.request.AttemptRequest;
import com.learnvault.assessmentevaluation.dto.response.AttemptCountResponse;
import com.learnvault.assessmentevaluation.dto.response.AttemptResponse;
import com.learnvault.assessmentevaluation.entity.Assessment;
import com.learnvault.assessmentevaluation.entity.AttemptRecord;
import com.learnvault.assessmentevaluation.exception.BadRequestException;
import com.learnvault.assessmentevaluation.exception.ResourceNotFoundException;
import com.learnvault.assessmentevaluation.repository.AssessmentRepository;
import com.learnvault.assessmentevaluation.repository.AttemptRepository;
import com.learnvault.assessmentevaluation.service.AttemptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttemptServiceImpl implements AttemptService {

    private final AttemptRepository attemptRepository;
    private final AssessmentRepository assessmentRepository;

    @Override
    @Transactional
    public AttemptResponse submitAttempt(AttemptRequest request) {
        log.info("Submitting attempt for assessment: {}, learner: {}", request.getAssessmentId(), request.getLearnerId());

        Assessment assessment = assessmentRepository.findById(request.getAssessmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found with id: " + request.getAssessmentId()));

        long attemptCount = attemptRepository.countByAssessment_AssessmentIdAndLearnerId(
                request.getAssessmentId(), request.getLearnerId());

        if (attemptCount >= assessment.getMaxAttempts()) {
            throw new BadRequestException("Maximum attempts exceeded for this assessment");
        }

        boolean passed = request.getScore() >= assessment.getPassingMarks();

        AttemptRecord attempt = AttemptRecord.builder()
                .assessment(assessment)
                .learnerId(request.getLearnerId())
                .attemptNumber((int) attemptCount + 1)
                .score(request.getScore())
                .passed(passed)
                .timeTakenMinutes(request.getTimeTakenMinutes())
                .build();

        AttemptRecord saved = attemptRepository.save(attempt);
        log.info("Attempt saved - Score: {}, Passed: {}", saved.getScore(), saved.getPassed());
        return mapToResponse(saved);
    }

    @Override
    public List<AttemptResponse> getAttemptHistory(Integer assessmentId, Integer learnerId) {
        log.info("Fetching attempt history - assessment: {}, learner: {}", assessmentId, learnerId);
        return attemptRepository.findByAssessment_AssessmentIdAndLearnerId(assessmentId, learnerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AttemptCountResponse getAttemptCount(Integer assessmentId, Integer learnerId) {
        long count = attemptRepository.countByAssessment_AssessmentIdAndLearnerId(assessmentId, learnerId);
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found with id: " + assessmentId));
        
        return AttemptCountResponse.builder()
                .attemptCount(count)
                .remaining(Math.max(0, assessment.getMaxAttempts() - (int) count))
                .build();
    }

    private AttemptResponse mapToResponse(AttemptRecord attempt) {
        return AttemptResponse.builder()
                .attemptId(attempt.getAttemptId())
                .assessmentId(attempt.getAssessment().getAssessmentId())
                .learnerId(attempt.getLearnerId())
                .attemptNumber(attempt.getAttemptNumber())
                .score(attempt.getScore())
                .passed(attempt.getPassed())
                .attemptDate(attempt.getAttemptDate())
                .timeTakenMinutes(attempt.getTimeTakenMinutes())
                .build();
    }

    @Override
    public List<AttemptResponse> getAllAttempts() {
        log.info("Fetching all attempts");
        return attemptRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}
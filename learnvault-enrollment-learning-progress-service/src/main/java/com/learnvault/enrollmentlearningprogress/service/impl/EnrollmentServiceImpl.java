package com.learnvault.enrollmentlearningprogress.service.impl;

import com.learnvault.enrollmentlearningprogress.client.CertificationBadgeClient;
import com.learnvault.enrollmentlearningprogress.client.NotificationAlertClient;
import com.learnvault.enrollmentlearningprogress.dto.request.CertificationRequest;
import com.learnvault.enrollmentlearningprogress.dto.request.EnrollmentRequest;
import com.learnvault.enrollmentlearningprogress.dto.request.NotificationRequest;
import com.learnvault.enrollmentlearningprogress.dto.response.EnrollmentResponse;
import com.learnvault.enrollmentlearningprogress.entity.Enrollment;
import com.learnvault.enrollmentlearningprogress.entity.ModuleProgress;
import com.learnvault.enrollmentlearningprogress.entity.enums.EnrollmentStatus;
import com.learnvault.enrollmentlearningprogress.entity.enums.ModuleProgressStatus;
import com.learnvault.enrollmentlearningprogress.exception.BadRequestException;
import com.learnvault.enrollmentlearningprogress.exception.ResourceNotFoundException;
import com.learnvault.enrollmentlearningprogress.repository.EnrollmentRepository;
import com.learnvault.enrollmentlearningprogress.repository.ModuleProgressRepository;
import com.learnvault.enrollmentlearningprogress.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final ModuleProgressRepository moduleProgressRepository;
    private final CertificationBadgeClient certificationBadgeClient;
    private final NotificationAlertClient notificationAlertClient;

    @Override
    @Transactional
    public EnrollmentResponse enrollLearner(EnrollmentRequest request) {
        log.info("Enrolling learner {} in course {}", request.getLearnerId(), request.getCourseId());

        enrollmentRepository.findByLearnerIdAndCourseId(request.getLearnerId(), request.getCourseId())
                .ifPresent(e -> {
                    throw new BadRequestException("Learner already enrolled in this course");
                });

        Enrollment enrollment = Enrollment.builder()
                .learnerId(request.getLearnerId())
                .courseId(request.getCourseId())
                .deadlineDate(request.getDeadlineDate())
                .status(EnrollmentStatus.ENROLLED)
                .build();

        Enrollment saved = enrollmentRepository.save(enrollment);

        // Create module progress entries (placeholder - in real scenario fetch modules from CCM)
        // For now, auto-create a dummy entry or leave for manual creation

        // Send notification
        notificationAlertClient.sendNotification(NotificationRequest.builder()
                .userId(request.getLearnerId())
                .message("You have been enrolled in course " + request.getCourseId())
                .category("ENROLLMENT")
                .build());

        return mapToResponse(saved);
    }

    @Override
    public EnrollmentResponse getEnrollmentById(Integer id) {
        log.info("Fetching enrollment by ID: {}", id);
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + id));
        return mapToResponse(enrollment);
    }

    @Override
    public List<EnrollmentResponse> getEnrollmentsByLearner(Integer learnerId) {
        log.info("Fetching enrollments for learner: {}", learnerId);
        return enrollmentRepository.findByLearnerId(learnerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<EnrollmentResponse> getEnrollmentsByCourse(Integer courseId) {
        log.info("Fetching enrollments for course: {}", courseId);
        return enrollmentRepository.findByCourseId(courseId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EnrollmentResponse updateProgress(Integer enrollmentId, Integer moduleId, Integer percent, Integer timeSpentMinutes) {
        log.info("Updating progress for enrollment: {}, module: {}", enrollmentId, moduleId);

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));

        ModuleProgress progress = moduleProgressRepository
                .findByEnrollment_EnrollmentIdAndModuleId(enrollmentId, moduleId)
                .orElseGet(() -> ModuleProgress.builder()
                        .enrollment(enrollment)
                        .moduleId(moduleId)
                        .status(ModuleProgressStatus.INPROGRESS)
                        .startedDate(LocalDate.now())
                        .build());

        progress.setTimeSpentMinutes(progress.getTimeSpentMinutes() + (timeSpentMinutes != null ? timeSpentMinutes : 0));
        moduleProgressRepository.save(progress);

        enrollment.setProgressPercent(percent);
        if (percent >= 100) {
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
        } else {
            enrollment.setStatus(EnrollmentStatus.INPROGRESS);
        }

        Enrollment updated = enrollmentRepository.save(enrollment);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public EnrollmentResponse completeCourse(Integer enrollmentId) {
        log.info("Completing course for enrollment: {}", enrollmentId);

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));

        enrollment.setStatus(EnrollmentStatus.COMPLETED);
        enrollment.setCompletionDate(LocalDate.now());
        enrollment.setProgressPercent(100);
        Enrollment saved = enrollmentRepository.save(enrollment);

        // Auto-issue certificate via Feign
        certificationBadgeClient.issueCertificate(CertificationRequest.builder()
                .courseId(enrollment.getCourseId())
                .learnerId(enrollment.getLearnerId())
                .build());

        // Send notification
        notificationAlertClient.sendNotification(NotificationRequest.builder()
                .userId(enrollment.getLearnerId())
                .message("Congratulations! You completed the course.")
                .category("CERTIFICATION")
                .build());

        return mapToResponse(saved);
    }

    private EnrollmentResponse mapToResponse(Enrollment enrollment) {
        return EnrollmentResponse.builder()
                .enrollmentId(enrollment.getEnrollmentId())
                .learnerId(enrollment.getLearnerId())
                .courseId(enrollment.getCourseId())
                .enrollmentDate(enrollment.getEnrollmentDate())
                .deadlineDate(enrollment.getDeadlineDate())
                .completionDate(enrollment.getCompletionDate())
                .progressPercent(enrollment.getProgressPercent())
                .status(enrollment.getStatus())
                .build();
    }

    @Override
    public List<EnrollmentResponse> getAllEnrollments() {
        log.info("Fetching all enrollments");
        return enrollmentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}
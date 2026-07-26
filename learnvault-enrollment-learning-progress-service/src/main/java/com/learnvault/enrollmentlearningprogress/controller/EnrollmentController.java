package com.learnvault.enrollmentlearningprogress.controller;

import com.learnvault.enrollmentlearningprogress.dto.request.EnrollmentRequest;
import com.learnvault.enrollmentlearningprogress.dto.request.ProgressUpdateRequest;
import com.learnvault.enrollmentlearningprogress.dto.response.EnrollmentResponse;
import com.learnvault.enrollmentlearningprogress.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<EnrollmentResponse> enrollLearner(@RequestBody EnrollmentRequest request) {
        return new ResponseEntity<>(enrollmentService.enrollLearner(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EnrollmentResponse>> getEnrollments(
            @RequestParam(required = false) Integer learnerId,
            @RequestParam(required = false) Integer courseId) {
        if (learnerId != null) {
            return ResponseEntity.ok(enrollmentService.getEnrollmentsByLearner(learnerId));
        }
        if (courseId != null) {
            return ResponseEntity.ok(enrollmentService.getEnrollmentsByCourse(courseId));
        }
        // FIXED: return all enrollments instead of empty list
        return ResponseEntity.ok(enrollmentService.getAllEnrollments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentResponse> getEnrollmentById(@PathVariable Integer id) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentById(id));
    }

    @PatchMapping("/{id}/progress")
    public ResponseEntity<EnrollmentResponse> updateProgress(
            @PathVariable Integer id,
            @RequestBody ProgressUpdateRequest request) {
        return ResponseEntity.ok(enrollmentService.updateProgress(
                id, request.getModuleId(), request.getPercent(), request.getTimeSpentMinutes()));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<EnrollmentResponse> completeCourse(@PathVariable Integer id) {
        return ResponseEntity.ok(enrollmentService.completeCourse(id));
    }
}
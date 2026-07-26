package com.learnvault.instructorsessionmanagement.controller;

import com.learnvault.instructorsessionmanagement.dto.request.TrainingSessionRequest;
import com.learnvault.instructorsessionmanagement.dto.response.CapacityResponse;
import com.learnvault.instructorsessionmanagement.dto.response.TrainingSessionResponse;
import com.learnvault.instructorsessionmanagement.service.TrainingSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/training-sessions")
@RequiredArgsConstructor
public class TrainingSessionController {

    private final TrainingSessionService trainingSessionService;

    @PostMapping
    public ResponseEntity<TrainingSessionResponse> scheduleSession(@RequestBody TrainingSessionRequest request) {
        return new ResponseEntity<>(trainingSessionService.scheduleSession(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TrainingSessionResponse>> getAllSessions(
            @RequestParam(required = false) Integer courseId,
            @RequestParam(required = false) Integer instructorId) {
        return ResponseEntity.ok(trainingSessionService.getAllSessions(courseId, instructorId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainingSessionResponse> getSessionById(@PathVariable Integer id) {
        return ResponseEntity.ok(trainingSessionService.getSessionById(id));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<TrainingSessionResponse> cancelSession(@PathVariable Integer id) {
        return ResponseEntity.ok(trainingSessionService.cancelSession(id));
    }

    @GetMapping("/{id}/capacity")
    public ResponseEntity<CapacityResponse> getCapacity(@PathVariable Integer id) {
        return ResponseEntity.ok(trainingSessionService.getCapacity(id));
    }
}
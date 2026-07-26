package com.learnvault.instructorsessionmanagement.controller;

import com.learnvault.instructorsessionmanagement.dto.request.SessionRegistrationRequest;
import com.learnvault.instructorsessionmanagement.dto.response.SessionRegistrationResponse;
import com.learnvault.instructorsessionmanagement.service.SessionRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController 
@RequestMapping("/api/session-registrations")
@RequiredArgsConstructor
public class SessionRegistrationController {

    private final SessionRegistrationService sessionRegistrationService;

    @PostMapping
    public ResponseEntity<SessionRegistrationResponse> registerLearner(@RequestBody SessionRegistrationRequest request) {
        return new ResponseEntity<>(sessionRegistrationService.registerLearner(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SessionRegistrationResponse>> getRegistrationsBySession(
            @RequestParam Integer sessionId) {
        return ResponseEntity.ok(sessionRegistrationService.getRegistrationsBySession(sessionId));
    }

    @PatchMapping("/{id}/attendance")
    public ResponseEntity<SessionRegistrationResponse> markAttendance(@PathVariable Integer id,
                                                                        @RequestParam String status) {
        return ResponseEntity.ok(sessionRegistrationService.markAttendance(id, status));
    }
}
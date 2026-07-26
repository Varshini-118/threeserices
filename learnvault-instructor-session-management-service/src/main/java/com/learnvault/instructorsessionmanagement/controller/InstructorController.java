package com.learnvault.instructorsessionmanagement.controller;

import com.learnvault.instructorsessionmanagement.dto.request.InstructorRequest;
import com.learnvault.instructorsessionmanagement.dto.response.InstructorResponse;
import com.learnvault.instructorsessionmanagement.service.InstructorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instructors")
@RequiredArgsConstructor
public class InstructorController {

    private final InstructorService instructorService;

    @PostMapping
    public ResponseEntity<InstructorResponse> registerInstructor(@RequestBody InstructorRequest request) {
        return new ResponseEntity<>(instructorService.registerInstructor(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<InstructorResponse>> getAllInstructors() {
        return ResponseEntity.ok(instructorService.getAllInstructors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstructorResponse> getInstructorById(@PathVariable Integer id) {
        return ResponseEntity.ok(instructorService.getInstructorById(id));
    }

    @PatchMapping("/{id}/rating")
    public ResponseEntity<InstructorResponse> updateRating(@PathVariable Integer id,
                                                           @RequestParam Double value) {
        return ResponseEntity.ok(instructorService.updateRating(id, value));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<InstructorResponse> updateStatus(@PathVariable Integer id,
                                                           @RequestParam String status) {
        return ResponseEntity.ok(instructorService.updateStatus(id, status));
    }
}
package com.learnvault.instructorsessionmanagement.service.impl;

import com.learnvault.instructorsessionmanagement.client.UserClient;
import com.learnvault.instructorsessionmanagement.dto.request.InstructorRequest;
import com.learnvault.instructorsessionmanagement.dto.response.InstructorResponse;
import com.learnvault.instructorsessionmanagement.entity.Instructor;
import com.learnvault.instructorsessionmanagement.entity.enums.Status;
import com.learnvault.instructorsessionmanagement.exception.ResourceNotFoundException;
import com.learnvault.instructorsessionmanagement.repository.InstructorRepository;
import com.learnvault.instructorsessionmanagement.service.InstructorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.learnvault.instructorsessionmanagement.client.UserClient;
import com.learnvault.instructorsessionmanagement.dto.response.UserResponse;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstructorServiceImpl implements InstructorService {

    private final InstructorRepository instructorRepository;
    private final UserClient userClient;

    @Override
    public InstructorResponse registerInstructor(InstructorRequest request) {
        log.info("Registering instructor for user: {}", request.getUserId());

        Instructor instructor = Instructor.builder()
                .userId(request.getUserId())
                .specializations(request.getSpecializations())
                .qualificationLevel(request.getQualificationLevel())
                .ratingAvg(0.00)
                .status(Status.ACTIVE)
                .build();

        Instructor saved = instructorRepository.save(instructor);
        return mapToResponse(saved);
    }

    @Override
    public InstructorResponse getInstructorById(Integer id) {
        log.info("Fetching instructor by ID: {}", id);
        Instructor instructor = instructorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + id));
        return mapToResponse(instructor);
    }

    @Override
    public List<InstructorResponse> getAllInstructors() {
        log.info("Fetching all instructors");
        return instructorRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public InstructorResponse updateRating(Integer id, Double rating) {
        log.info("Updating instructor {} rating to {}", id, rating);
        Instructor instructor = instructorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + id));
        instructor.setRatingAvg(rating);
        Instructor updated = instructorRepository.save(instructor);
        return mapToResponse(updated);
    }

    @Override
    public InstructorResponse updateStatus(Integer id, String status) {
        log.info("Updating instructor {} status to {}", id, status);
        Instructor instructor = instructorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + id));
        instructor.setStatus(Status.valueOf(status));
        Instructor updated = instructorRepository.save(instructor);
        return mapToResponse(updated);
    }

    private InstructorResponse mapToResponse(Instructor instructor) {

        UserResponse user =
                userClient.getUserById(instructor.getUserId());

        return InstructorResponse.builder()
                .instructorName(user.getName())
                .instructorId(instructor.getInstructorId())
                .userId(instructor.getUserId())
                .specializations(instructor.getSpecializations())
                .qualificationLevel(instructor.getQualificationLevel())
                .ratingAvg(instructor.getRatingAvg())
                .status(instructor.getStatus())
                .build();
    }
}
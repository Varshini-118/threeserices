package com.learnvault.instructorsessionmanagement.service;

import com.learnvault.instructorsessionmanagement.dto.request.InstructorRequest;
import com.learnvault.instructorsessionmanagement.dto.response.InstructorResponse;

import java.util.List;

public interface InstructorService {
    InstructorResponse registerInstructor(InstructorRequest request);
    InstructorResponse getInstructorById(Integer id);
    List<InstructorResponse> getAllInstructors();
    InstructorResponse updateRating(Integer id, Double rating);
    InstructorResponse updateStatus(Integer id, String status);
}
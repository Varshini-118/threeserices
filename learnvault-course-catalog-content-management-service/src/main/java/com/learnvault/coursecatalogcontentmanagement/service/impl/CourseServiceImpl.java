package com.learnvault.coursecatalogcontentmanagement.service.impl;

import com.learnvault.coursecatalogcontentmanagement.dto.request.CourseRequest;
import com.learnvault.coursecatalogcontentmanagement.dto.response.CourseResponse;
import com.learnvault.coursecatalogcontentmanagement.entity.Course;
import com.learnvault.coursecatalogcontentmanagement.entity.enums.CourseLevel;
import com.learnvault.coursecatalogcontentmanagement.entity.enums.CourseStatus;
import com.learnvault.coursecatalogcontentmanagement.exception.BadRequestException;
import com.learnvault.coursecatalogcontentmanagement.exception.ResourceNotFoundException;
import com.learnvault.coursecatalogcontentmanagement.repository.CourseRepository;
import com.learnvault.coursecatalogcontentmanagement.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    @Override
    public CourseResponse createCourse(CourseRequest request) {
        log.info("Creating course: {}", request.getTitle());

        Course course = Course.builder()
                .title(request.getTitle())
                .category(request.getCategory())
                .level(request.getLevel())
                .durationHours(request.getDurationHours())
                .instructorId(request.getInstructorId())
                .deliveryMode(request.getDeliveryMode())
                .status(CourseStatus.DRAFT)
                .build();

        Course saved = courseRepository.save(course);
        log.info("Course created with ID: {}", saved.getCourseId());
        return mapToResponse(saved);
    }

    @Override
    public CourseResponse getCourseById(Integer id) {
        log.info("Fetching course by ID: {}", id);
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        return mapToResponse(course);
    }

    @Override
    public List<CourseResponse> getAllCourses(CourseStatus status, String category, CourseLevel level) {
        log.info("Fetching courses with filters - status: {}, category: {}, level: {}", status, category, level);
        
        List<Course> courses;
        if (status != null) {
            courses = courseRepository.findByStatus(status);
        } else if (category != null) {
            courses = courseRepository.findByCategory(category);
        } else if (level != null) {
            courses = courseRepository.findByLevel(level);
        } else {
            courses = courseRepository.findAll();
        }
        
        return courses.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public CourseResponse publishCourse(Integer id) {
        log.info("Publishing course: {}", id);
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        
        if (course.getStatus() != CourseStatus.DRAFT) {
            throw new BadRequestException("Only DRAFT courses can be published");
        }
        
        course.setStatus(CourseStatus.PUBLISHED);
        Course updated = courseRepository.save(course);
        return mapToResponse(updated);
    }

    @Override
    public CourseResponse archiveCourse(Integer id) {
        log.info("Archiving course: {}", id);
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        
        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new BadRequestException("Only PUBLISHED courses can be archived");
        }
        
        course.setStatus(CourseStatus.ARCHIVED);
        Course updated = courseRepository.save(course);
        return mapToResponse(updated);
    }

    @Override
    public void deleteCourse(Integer id) {
        log.info("Deleting course: {}", id);
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        courseRepository.delete(course);
    }

    private CourseResponse mapToResponse(Course course) {
        return CourseResponse.builder()
                .courseId(course.getCourseId())
                .title(course.getTitle())
                .category(course.getCategory())
                .level(course.getLevel())
                .durationHours(course.getDurationHours())
                .instructorId(course.getInstructorId())
                .deliveryMode(course.getDeliveryMode())
                .status(course.getStatus())
                .build();
    }
}
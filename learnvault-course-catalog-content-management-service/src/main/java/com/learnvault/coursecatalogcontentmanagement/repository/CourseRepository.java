package com.learnvault.coursecatalogcontentmanagement.repository;

import com.learnvault.coursecatalogcontentmanagement.entity.Course;
import com.learnvault.coursecatalogcontentmanagement.entity.enums.CourseLevel;
import com.learnvault.coursecatalogcontentmanagement.entity.enums.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    List<Course> findByStatus(CourseStatus status);
    List<Course> findByCategory(String category);
    List<Course> findByLevel(CourseLevel level);
}
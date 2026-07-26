package com.learnvault.instructorsessionmanagement.repository;

import com.learnvault.instructorsessionmanagement.entity.Instructor;
import com.learnvault.instructorsessionmanagement.entity.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Integer> {
    List<Instructor> findByStatus(Status status);
    Optional<Instructor> findByUserId(Integer userId);
}
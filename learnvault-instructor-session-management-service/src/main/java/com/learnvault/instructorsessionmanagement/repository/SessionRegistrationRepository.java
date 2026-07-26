package com.learnvault.instructorsessionmanagement.repository;

import com.learnvault.instructorsessionmanagement.entity.SessionRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRegistrationRepository extends JpaRepository<SessionRegistration, Integer> {
    List<SessionRegistration> findBySession_SessionId(Integer sessionId);
    List<SessionRegistration> findByLearnerId(Integer learnerId);
    long countBySession_SessionId(Integer sessionId);
}
package com.learnvault.certificationbadgemanagement.service.impl;

import com.learnvault.certificationbadgemanagement.dto.request.CertificationRequest;
import com.learnvault.certificationbadgemanagement.dto.response.CertificationResponse;
import com.learnvault.certificationbadgemanagement.entity.Certification;
import com.learnvault.certificationbadgemanagement.entity.enums.CertificationStatus;
import com.learnvault.certificationbadgemanagement.exception.ResourceNotFoundException;
import com.learnvault.certificationbadgemanagement.repository.CertificationRepository;
import com.learnvault.certificationbadgemanagement.service.CertificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CertificationServiceImpl implements CertificationService {

    private final CertificationRepository certificationRepository;

    @Override
    public CertificationResponse issueCertification(CertificationRequest request) {
        log.info("Issuing certification for learner {} in course {}", request.getLearnerId(), request.getCourseId());

        String certNumber = "LVC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Certification certification = Certification.builder()
                .courseId(request.getCourseId())
                .learnerId(request.getLearnerId())
                .issuedDate(LocalDate.now())
                .expiryDate(request.getExpiryDate())
                .certificateNumber(certNumber)
                .status(CertificationStatus.VALID)
                .build();

        Certification saved = certificationRepository.save(certification);
        return mapToResponse(saved);
    }

    @Override
    public CertificationResponse getCertificationById(Integer id) {
        log.info("Fetching certification by ID: {}", id);
        Certification cert = certificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certification not found with id: " + id));
        return mapToResponse(cert);
    }

    @Override
    public List<CertificationResponse> getCertificationsByLearner(Integer learnerId) {
        log.info("Fetching certifications for learner: {}", learnerId);
        return certificationRepository.findByLearnerId(learnerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CertificationResponse revokeCertification(Integer id) {
        log.info("Revoking certification: {}", id);
        Certification cert = certificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certification not found with id: " + id));
        cert.setStatus(CertificationStatus.REVOKED);
        Certification updated = certificationRepository.save(cert);
        return mapToResponse(updated);
    }

    private CertificationResponse mapToResponse(Certification cert) {
        return CertificationResponse.builder()
                .certificationId(cert.getCertificationId())
                .courseId(cert.getCourseId())
                .learnerId(cert.getLearnerId())
                .issuedDate(cert.getIssuedDate())
                .expiryDate(cert.getExpiryDate())
                .certificateNumber(cert.getCertificateNumber())
                .status(cert.getStatus())
                .build();
    }
}
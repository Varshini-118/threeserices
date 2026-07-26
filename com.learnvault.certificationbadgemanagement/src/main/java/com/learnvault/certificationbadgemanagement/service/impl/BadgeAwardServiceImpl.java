package com.learnvault.certificationbadgemanagement.service.impl;

import com.learnvault.certificationbadgemanagement.dto.request.BadgeAwardRequest;
import com.learnvault.certificationbadgemanagement.dto.response.BadgeAwardResponse;
import com.learnvault.certificationbadgemanagement.entity.Badge;
import com.learnvault.certificationbadgemanagement.entity.BadgeAward;
import com.learnvault.certificationbadgemanagement.entity.enums.BadgeAwardStatus;
import com.learnvault.certificationbadgemanagement.exception.ResourceNotFoundException;
import com.learnvault.certificationbadgemanagement.repository.BadgeAwardRepository;
import com.learnvault.certificationbadgemanagement.repository.BadgeRepository;
import com.learnvault.certificationbadgemanagement.service.BadgeAwardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BadgeAwardServiceImpl implements BadgeAwardService {

    private final BadgeAwardRepository badgeAwardRepository;
    private final BadgeRepository badgeRepository;

    @Override
    public BadgeAwardResponse awardBadge(BadgeAwardRequest request) {
        log.info("Awarding badge {} to learner {}", request.getBadgeId(), request.getLearnerId());

        Badge badge = badgeRepository.findById(request.getBadgeId())
                .orElseThrow(() -> new ResourceNotFoundException("Badge not found with id: " + request.getBadgeId()));

        BadgeAward award = BadgeAward.builder()
                .badge(badge)
                .learnerId(request.getLearnerId())
                .status(BadgeAwardStatus.ACTIVE)
                .build();

        BadgeAward saved = badgeAwardRepository.save(award);
        return mapToResponse(saved);
    }

    @Override
    public List<BadgeAwardResponse> getAwardsByLearner(Integer learnerId) {
        log.info("Fetching badge awards for learner: {}", learnerId);
        return badgeAwardRepository.findByLearnerId(learnerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private BadgeAwardResponse mapToResponse(BadgeAward award) {
        return BadgeAwardResponse.builder()
                .awardId(award.getAwardId())
                .badgeId(award.getBadge().getBadgeId())
                .badgeName(award.getBadge().getName())
                .learnerId(award.getLearnerId())
                .awardedDate(award.getAwardedDate())
                .status(award.getStatus())
                .build();
    }
}
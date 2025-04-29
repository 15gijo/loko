package com.team15gijo.follow.domain.service.impl;

import com.team15gijo.common.exception.CustomException;
import com.team15gijo.follow.application.dto.v2.FollowCursorRecommendCommand;
import com.team15gijo.follow.domain.exception.FollowDomainExceptionCode;
import com.team15gijo.follow.domain.model.FollowEntity;
import com.team15gijo.follow.domain.model.FollowStatus;
import com.team15gijo.follow.domain.model.RecommendPriority;
import com.team15gijo.follow.domain.repository.FollowRepository;
import com.team15gijo.follow.domain.service.FollowDomainService;
import com.team15gijo.follow.infrastructure.dto.response.v2.UserAndRegionInfoFollowResponseDto;
import com.team15gijo.follow.presentation.dto.request.v2.FollowRequestDto;
import com.team15gijo.follow.presentation.dto.response.v2.FollowRecommendResponseDto;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowDomainServiceImpl implements FollowDomainService {

    private final FollowRepository followRepository;

    @Override
    public FollowEntity createOrRestoreFollow(Long followerId, FollowRequestDto followRequestDto) {
        //자기 자신 팔로우 금지
        if (followerId.equals(followRequestDto.followeeId())) {
            throw new CustomException(FollowDomainExceptionCode.CANNOT_FOLLOW_SELF);
        }

        //블락 체크
        boolean isBlockedTarget = followRepository.existsByFollowerIdAndFolloweeIdAndFollowStatus(
                followerId, followRequestDto.followeeId(), FollowStatus.BLOCKED);
        boolean targetBlockedMe = followRepository.existsByFollowerIdAndFolloweeIdAndFollowStatus(
                followRequestDto.followeeId(), followerId, FollowStatus.BLOCKED);
        if (isBlockedTarget || targetBlockedMe) {
            throw new CustomException(FollowDomainExceptionCode.CANNOT_FOLLOW_BLOCKED_USER);
        }

        //softDelete(언팔로우 복구)
        Optional<FollowEntity> deletedFollowOpt = followRepository.findDeletedByFollowerIdAndFolloweeId(
                followerId, followRequestDto.followeeId());
        if (deletedFollowOpt.isPresent()) {
            FollowEntity deletedFollow = deletedFollowOpt.get();
            followRepository.restoreFollow(deletedFollow.getId());
            return deletedFollow;
        }

        //중복 체크
        boolean isExists = followRepository.existsByFollowerIdAndFolloweeId(followerId,
                followRequestDto.followeeId());
        if (isExists) {
            throw new CustomException(FollowDomainExceptionCode.ALREADY_FOLLOWING);
        }

        return FollowEntity.builder()
                .followerId(followerId)
                .followeeId(followRequestDto.followeeId())
                .followStatus(FollowStatus.FOLLOW)
                .build();
    }

    @Override
    public FollowEntity deleteFollow(Long followerId, Long followeeId) {
        //팔로우 확인
        FollowEntity follow = checkFollow(followerId, followeeId);

        //상태 변경
        follow.unfollow();

        return follow;
    }

    @Override
    public FollowEntity blockFollow(Long followerId, Long followeeId) {
        //자기 자신 블락 금지
        if (followerId.equals(followeeId)) {
            throw new CustomException(FollowDomainExceptionCode.CANNOT_BLOCK_SELF);
        }

        //팔로우 확인
        FollowEntity checkedFollow = checkFollow(followerId, followeeId);

        //블락 중복 확인
        if (checkedFollow.getFollowStatus() == FollowStatus.BLOCKED) {
            throw new CustomException(FollowDomainExceptionCode.ALREADY_BLOCKED);
        }

        checkedFollow.block();

        return checkedFollow;
    }

    @Override
    public FollowEntity unblockFollow(Long followerId, Long followeeId) {
        //팔로우 확인
        FollowEntity checkedFollow = checkFollow(followerId, followeeId);

        //언블락 체크
        if (checkedFollow.getFollowStatus() != FollowStatus.BLOCKED) {
            throw new CustomException(FollowDomainExceptionCode.NOT_BLOCKED);
        }

        //팔로우 복구
        checkedFollow.follow();

        return checkedFollow;
    }

    @Override
    public Page<FollowEntity> getMyFollowings(Long followerId, Pageable validatePageable) {
        return followRepository.findAllByFollowerIdAndFollowStatus(
                followerId,
                FollowStatus.FOLLOW,
                validatePageable
        );
    }

    @Override
    public Page<FollowEntity> getMyFollowers(Long followeeId, Pageable validatePageable) {
        return followRepository.findAllByFolloweeIdAndFollowStatus(
                followeeId,
                FollowStatus.FOLLOW,
                validatePageable
        );
    }

    @Override
    public long countFollowers(Long followeeId) {
        return followRepository.countByFolloweeIdAndFollowStatus(followeeId, FollowStatus.FOLLOW);
    }

    @Override
    public long countFollowings(Long followerId) {
        return followRepository.countByFollowerIdAndFollowStatus(followerId, FollowStatus.FOLLOW);
    }

    @Override
    public List<FollowRecommendResponseDto> recommend(
            FollowCursorRecommendCommand followCursorRecommendCommand) {
        //후보 리스트
        List<UserAndRegionInfoFollowResponseDto> candidateList = followCursorRecommendCommand.getUserAndRegionInfos();

        //후보 리스트 response로 변환
        List<FollowRecommendResponseDto> recommendResponseDtoList = candidateList.stream()
                .map(candidate -> new FollowRecommendResponseDto(
                        candidate.userId(),
                        candidate.nickname(),
                        candidate.username(),
                        candidate.profile(),
                        candidate.regionName(),
                        0
                ))
                .toList();

        //우선순위 계산
        recommendResponseDtoList.sort((a, b) -> Integer.compare(
                calcScore(followCursorRecommendCommand, b), //내림차순
                calcScore(followCursorRecommendCommand, a)
        ));

        return recommendResponseDtoList;
    }

    private int calcScore(FollowCursorRecommendCommand followCursorRecommendCommand,
            FollowRecommendResponseDto candidate) {
        int score = 0;

        String myRegionCode = null;
        String candidateRegionCode = null;
        Double candidateDistanceKm = null;

        for (UserAndRegionInfoFollowResponseDto dto : followCursorRecommendCommand.getUserAndRegionInfos()) {
            if (dto.userId().equals(followCursorRecommendCommand.getUserId())) {
                myRegionCode = dto.regionName();
            }
            if (dto.userId().equals(candidate.userId())) {
                candidateRegionCode = dto.regionName();
                candidateDistanceKm = dto.distanceKm();
            }
            if (myRegionCode != null && candidateRegionCode != null) {
                break;
            }
        }

        if (myRegionCode == null && candidateRegionCode == null) {
            return score;
        }

        //지역 점수
        if (followCursorRecommendCommand.getRecommendPriority() == RecommendPriority.REGION) {
            if (safeSubstring(myRegionCode, 8).equals(safeSubstring(candidateRegionCode, 8))) {
                score += 100;
            } else if (safeSubstring(myRegionCode, 5).equals(
                    safeSubstring(candidateRegionCode, 5))) {
                score += 50;
            } else if (safeSubstring(myRegionCode, 2).equals(
                    safeSubstring(candidateRegionCode, 2))) {
                score += 20;
            }
        }

        //거리 기반 점수
        if (candidateDistanceKm != null) {
            if (candidateDistanceKm <= 1.0) {
                score += 80;
            } else if (candidateDistanceKm <= 5.0) {
                score += 50;
            } else if (candidateDistanceKm <= 10.0) {
                score += 30;
            }
        }
        return score;
    }

    private String safeSubstring(String str, int end) {
        if (str == null || str.length() < end) {
            return str;
        }
        return str.substring(0, end);
    }


    private FollowEntity checkFollow(Long followerId, Long followeeId) {
        return followRepository.findByFollowerIdAndFolloweeId(followerId, followeeId)
                .orElseThrow(() -> new CustomException(FollowDomainExceptionCode.FOLLOW_NOT_FOUND));
    }
}

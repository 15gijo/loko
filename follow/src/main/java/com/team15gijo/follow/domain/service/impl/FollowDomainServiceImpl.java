package com.team15gijo.follow.domain.service.impl;

import com.team15gijo.common.exception.CustomException;
import com.team15gijo.follow.domain.exception.FollowDomainExceptionCode;
import com.team15gijo.follow.domain.model.FollowEntity;
import com.team15gijo.follow.domain.model.FollowStatus;
import com.team15gijo.follow.domain.repository.FollowRepository;
import com.team15gijo.follow.domain.service.FollowDomainService;
import com.team15gijo.follow.presentation.dto.request.v2.FollowRequestDto;
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

    private FollowEntity checkFollow(Long followerId, Long followeeId) {
        return followRepository.findByFollowerIdAndFolloweeId(followerId, followeeId)
                .orElseThrow(() -> new CustomException(FollowDomainExceptionCode.FOLLOW_NOT_FOUND));
    }
}

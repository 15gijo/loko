package com.team15gijo.follow.application.service.v2;

import com.team15gijo.follow.application.dto.v2.AdminFollowSearchCommand;
import com.team15gijo.follow.application.dto.v2.FollowCursorCandidateResult;
import com.team15gijo.follow.application.dto.v2.FollowCursorRecommendCommand;
import com.team15gijo.follow.application.service.FollowApplicationService;
import com.team15gijo.follow.domain.model.FollowEntity;
import com.team15gijo.follow.domain.model.FollowStatus;
import com.team15gijo.follow.domain.model.RecommendPriority;
import com.team15gijo.follow.domain.repository.FollowRepository;
import com.team15gijo.follow.domain.service.FollowDomainService;
import com.team15gijo.follow.infrastructure.client.UserFeignClient;
import com.team15gijo.follow.infrastructure.dto.request.UserAndRegionInfoRequestDto;
import com.team15gijo.follow.infrastructure.dto.response.v2.UserAndRegionInfoFollowResponseDto;
import com.team15gijo.follow.infrastructure.dto.response.v2.UserInfoFollowResponseDto;
import com.team15gijo.follow.infrastructure.kafka.event.publisher.FollowEventPublisher;
import com.team15gijo.follow.infrastructure.redis.repository.FollowRedisRepository;
import com.team15gijo.follow.presentation.dto.request.v2.BlockRequestDto;
import com.team15gijo.follow.presentation.dto.request.v2.FollowRequestDto;
import com.team15gijo.follow.presentation.dto.response.v2.AdminFollowSearchResponseDto;
import com.team15gijo.follow.presentation.dto.response.v2.BlockResponseDto;
import com.team15gijo.follow.presentation.dto.response.v2.FollowCountResponseDto;
import com.team15gijo.follow.presentation.dto.response.v2.FollowRecommendResponseDto;
import com.team15gijo.follow.presentation.dto.response.v2.FollowResponseDto;
import com.team15gijo.follow.presentation.dto.response.v2.FollowUserResponseDto;
import com.team15gijo.follow.presentation.dto.response.v2.UnblockResponseDto;
import com.team15gijo.follow.presentation.dto.response.v2.UnfollowResponseDto;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FollowApplicationServiceImpl implements FollowApplicationService {

    private final FollowDomainService followDomainService;
    private final FollowRepository followRepository;
    private final UserFeignClient userFeignClient;
    private final FollowEventPublisher followEventPublisher;
    private final FollowRedisRepository<String> followRedisRepository;

    @Override
    @Transactional
    public FollowResponseDto createFollow(Long followerId, FollowRequestDto followRequestDto) {

        FollowEntity follow = followDomainService.createOrRestoreFollow(followerId,
                followRequestDto);

        FollowEntity savedFollow = followRepository.save(follow);

        followEventPublisher.publishFollowCreated(savedFollow.getFollowerId(),
                savedFollow.getFolloweeId());

        return FollowResponseDto.from(savedFollow);
    }

    @Override
    @Transactional
    public UnfollowResponseDto deleteFollow(Long followerId, Long followeeId) {

        FollowEntity follow = followDomainService.deleteFollow(followerId, followeeId);

        //상태 업데이트 dirtyChecking 강제
        followRepository.saveAndFlush(follow);

        followRepository.delete(follow);

        followEventPublisher.publishFollowDeleted(follow.getFollowerId(), follow.getFolloweeId());

        return UnfollowResponseDto.from(follow);
    }

    @Override
    @Transactional
    public BlockResponseDto blockFollow(Long followerId, BlockRequestDto blockRequestDto) {

        FollowEntity follow = followDomainService.blockFollow(
                followerId,
                blockRequestDto.blockUserId());

        FollowEntity savedFollow = followRepository.save(follow);

        return BlockResponseDto.from(savedFollow);
    }

    @Override
    @Transactional
    public UnblockResponseDto unblockFollow(Long followerId, Long followeeId) {

        FollowEntity follow = followDomainService.unblockFollow(followerId, followeeId);

        FollowEntity savedFollow = followRepository.save(follow);

        return UnblockResponseDto.from(savedFollow);
    }

    @Override
    public Page<FollowUserResponseDto> getMyFollowings(Long followerId, Pageable validatePageable) {
        return getFollowingsInternal(followerId, validatePageable);
    }

    @Override
    public Page<FollowUserResponseDto> getMyFollowers(Long followeeId, Pageable validatePageable) {
        return getFollowersInternal(followeeId, validatePageable);
    }

    @Override
    public FollowCountResponseDto getCountMyFollows(Long userId) {
        return getCountFollowsInternal(userId);
    }

    @Override
    public Page<FollowUserResponseDto> getFollowings(Long targetUserId, Pageable validatePageable) {
        return getFollowingsInternal(targetUserId, validatePageable);
    }

    @Override
    public Page<FollowUserResponseDto> getFollowers(Long targetUserId, Pageable validatePageable) {
        return getFollowersInternal(targetUserId, validatePageable);
    }

    @Override
    public FollowCountResponseDto getCountFollowers(Long targetUserId) {
        return getCountFollowsInternal(targetUserId);
    }

    @Override
    public Page<AdminFollowSearchResponseDto> searchAllFollows(UUID followId, Long followerId,
            Long followeeId, FollowStatus followStatus, Pageable validatePageable) {
        AdminFollowSearchCommand adminFollowSearchCommand = AdminFollowSearchCommand.builder()
                .followId(followId)
                .followerId(followerId)
                .followeeId(followeeId)
                .followStatus(followStatus)
                .build();
        return followRepository.searchAllFollowsForAdmin(adminFollowSearchCommand,
                validatePageable);
    }

    @Override
    public Slice<FollowRecommendResponseDto> recommend(Long userId, Long lastUserId,
            RecommendPriority priority, Pageable validatePageable) {
        //팔로우 2hop 관계 아이디 조회
        FollowCursorCandidateResult candidateResult = followRepository.find2HopCandidateUserIds(
                userId, lastUserId,
                validatePageable);
        if (candidateResult.userIds().isEmpty()) {
            return new SliceImpl<>(Collections.emptyList(), validatePageable, false);
        }

        //유저 정보, 지역 정보 가져오기
        List<UserAndRegionInfoFollowResponseDto> userAndRegionInfos = userFeignClient.getUserAndRegionInfo(
                new UserAndRegionInfoRequestDto(userId, candidateResult.userIds())
        );
//                candidateResult.userIds(), userId);

        //유저 팔로우/팔로잉 count 가져오기
//        Map<Long, Integer> candidateCount = followRepository.countByCandidateUserIds(
//                candidateUserIds);

        List<FollowRecommendResponseDto> followRecommendResponseDtoList = followDomainService.recommend(
                FollowCursorRecommendCommand.of(
                        userId,
                        candidateResult.userIds(),
                        userAndRegionInfos,
                        null,
                        priority
                )
        );

        return new SliceImpl<>(followRecommendResponseDtoList, validatePageable,
                candidateResult.hasNext());
    }


    private Page<FollowUserResponseDto> getFollowingsInternal(Long followerId, Pageable pageable) {
        Page<FollowEntity> followEntityPages = followDomainService.getMyFollowings(followerId,
                pageable);

        return followEntityPages.map(followEntity -> {
            Long followeeId = followEntity.getFolloweeId();

            //팔로잉 유저 서버에서 정보 가져오기
            UserInfoFollowResponseDto userInfoFollowResponseDto = userFeignClient.getUserInfoFollowing(
                    followeeId);

            //맞팔 체크 (팔로워 아이디 <- 팔로잉 아이디 바꿔서 찾기)
            boolean isMutual = followRepository.existsByFollowerIdAndFolloweeId(followeeId,
                    followerId);

            return FollowUserResponseDto.of(
                    userInfoFollowResponseDto.userId(),
                    userInfoFollowResponseDto.username(),
                    userInfoFollowResponseDto.nickname(),
                    userInfoFollowResponseDto.profile(),
                    isMutual
            );
        });
    }

    private Page<FollowUserResponseDto> getFollowersInternal(Long followeeId, Pageable pageable) {
        Page<FollowEntity> followEntityPage = followDomainService.getMyFollowers(followeeId,
                pageable);

        return followEntityPage.map(followEntity -> {
            Long followerId = followEntity.getFollowerId();

            //팔로워 유저 정보 가져오기
            UserInfoFollowResponseDto userInfoFollowResponseDto = userFeignClient.getUserInfoFollower(
                    followerId);

            //맞팔 체크 (팔로워 아이디 -> 팔로잉 아이디 바꿔서 찾기)
            boolean isMutual = followRepository.existsByFollowerIdAndFolloweeId(followerId,
                    followeeId);

            return FollowUserResponseDto.of(
                    userInfoFollowResponseDto.userId(),
                    userInfoFollowResponseDto.username(),
                    userInfoFollowResponseDto.nickname(),
                    userInfoFollowResponseDto.profile(),
                    isMutual
            );
        });
    }

//    private FollowCountResponseDto getCountFollowsInternal(Long userId) {
//        long followerCount = followDomainService.countFollowers(userId);
//        long followingCount = followDomainService.countFollowings(userId);
//
//        return FollowCountResponseDto.of(
//                followerCount,
//                followingCount
//        );
//    }

//    private FollowCountResponseDto getCountFollowsInternal(Long userId) {
//        Integer followerCount = followRedisRepository.getFollowerCount(String.valueOf(userId))
//                .orElseGet(() -> {
//                    log.info("[/me/count] followerCount Redis miss 발생 - fallback 0 저장 (userId = {})", userId);
//                    followRedisRepository.saveFollowerCount(String.valueOf(userId), 0);
//                    return 0;
//                });
//
//        Integer followingCount = followRedisRepository.getFollowingCount(String.valueOf(userId))
//                .orElseGet(() -> {
//                    log.info("[/me/count] followingCount Redis miss 발생 - fallback 0 저장 (userId = {})", userId);
//                    followRedisRepository.saveFollowingCount(String.valueOf(userId), 0);
//                    return 0;
//                });
//
//        return FollowCountResponseDto.of(followerCount, followingCount);
//    }

    private FollowCountResponseDto getCountFollowsInternal(Long userId) {
        Long followerCount = followRedisRepository.getFollowerCountWithFallback(
                String.valueOf(userId),
                () -> followDomainService.countFollowers(userId)
        ).orElseGet(() -> {
            log.info("[Redisson Fallback Failed] followerCount Redis 및 lock 에러 발생 - fallback 0 저장 (userId = {})",
                    userId);
            followRedisRepository.saveFollowerCount(String.valueOf(userId), 0L);
            return 0L;
        });

        Long followingCount = followRedisRepository.getFollowingCountWithFallback(
                String.valueOf(userId),
                () -> followDomainService.countFollowings(userId)
        ).orElseGet(() -> {
            log.info(
                    "[Redisson Fallback Failed] followingCountRedis 및 lock 에러 발생 - fallback 0 저장 (userId = {})",
                    userId);
            followRedisRepository.saveFollowingCount(String.valueOf(userId), 0L);
            return 0L;
        });

        return FollowCountResponseDto.of(followerCount, followingCount);
    }


}

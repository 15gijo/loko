package com.team15gijo.follow.presentation.controller.v2;

import com.team15gijo.common.annotation.RoleGuard;
import com.team15gijo.common.dto.ApiResponse;
import com.team15gijo.common.utils.page.PageableUtils;
import com.team15gijo.follow.application.service.FollowApplicationService;
import com.team15gijo.follow.domain.model.FollowStatus;
import com.team15gijo.follow.presentation.dto.request.v2.BlockRequestDto;
import com.team15gijo.follow.presentation.dto.request.v2.FollowRequestDto;
import com.team15gijo.follow.presentation.dto.response.v2.AdminFollowSearchResponseDto;
import com.team15gijo.follow.presentation.dto.response.v2.BlockResponseDto;
import com.team15gijo.follow.presentation.dto.response.v2.FollowCountResponseDto;
import com.team15gijo.follow.presentation.dto.response.v2.FollowResponseDto;
import com.team15gijo.follow.presentation.dto.response.v2.FollowUserResponseDto;
import com.team15gijo.follow.presentation.dto.response.v2.UnblockResponseDto;
import com.team15gijo.follow.presentation.dto.response.v2.UnfollowResponseDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/follows")
public class FollowController {

    private final FollowApplicationService followApplicationService;


    //팔로우 하기
    @RoleGuard(min = "USER")
    @PostMapping
    public ResponseEntity<ApiResponse<FollowResponseDto>> createFollow(
            @RequestHeader("X-User-Id") Long followerId,
            @RequestBody FollowRequestDto followRequestDto
    ) {
        FollowResponseDto followResponseDto = followApplicationService.createFollow(
                followerId, followRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body((ApiResponse.success("팔로우 성공", followResponseDto)));
    }

    //언팔로우 하기
    @RoleGuard(min = "USER")
    @DeleteMapping("/{followeeId}")
    public ResponseEntity<ApiResponse<UnfollowResponseDto>> deleteFollow(
            @RequestHeader("X-User-Id") Long followerId,
            @PathVariable("followeeId") Long followeeId
    ) {
        UnfollowResponseDto unfollowResponseDto = followApplicationService.deleteFollow(
                followerId, followeeId);
        return ResponseEntity.ok(ApiResponse.success("언팔로우 성공", unfollowResponseDto));
    }

    //팔로우 차단하기
    @RoleGuard(min = "USER")
    @PostMapping("/block")
    public ResponseEntity<ApiResponse<BlockResponseDto>> blockFollow(
            @RequestHeader("X-User-Id") Long followerId,
            @RequestBody BlockRequestDto blockRequestDto
    ) {
        BlockResponseDto blockResponseDto = followApplicationService.blockFollow(
                followerId,
                blockRequestDto);
        return ResponseEntity.ok(ApiResponse.success("블락 성공", blockResponseDto));
    }

    //팔로우 차단해제
    @RoleGuard(min = "USER")
    @DeleteMapping("/block/{followeeId}")
    public ResponseEntity<ApiResponse<UnblockResponseDto>> unblockFollow(
            @RequestHeader("X-User-Id") Long followerId,
            @PathVariable("followeeId") Long followeeId
    ) {
        UnblockResponseDto unblockResponseDto = followApplicationService.unblockFollow(
                followerId,
                followeeId);
        return ResponseEntity.ok(ApiResponse.success("블락 해제 성공", unblockResponseDto));
    }

    //내 팔로잉 조회
    @RoleGuard(min = "USER")
    @GetMapping("/me/followings")
    public ResponseEntity<ApiResponse<Page<FollowUserResponseDto>>> getMyFollowings(
            @RequestHeader("X-User-Id") Long followerId,
            @PageableDefault(
                    size = 10,
                    page = 1,
                    sort = {"createdAt", "updatedAt"},
                    direction = Direction.ASC
            ) Pageable pageable
    ) {
        Pageable validatePageable = PageableUtils.validate(pageable);
        Page<FollowUserResponseDto> followUserResponseDto = followApplicationService.getMyFollowings(
                followerId, validatePageable);
        return ResponseEntity.ok(ApiResponse.success("내 팔로잉 조회 성공", followUserResponseDto));
    }

    //내 팔로워 조회
    @RoleGuard(min = "USER")
    @GetMapping("/me/followers")
    public ResponseEntity<ApiResponse<Page<FollowUserResponseDto>>> getMyFollowers(
            @RequestHeader("X-User-Id") Long followeeId,
            @PageableDefault(
                    size = 10,
                    page = 1,
                    sort = {"createdAt", "updatedAt"},
                    direction = Direction.ASC
            ) Pageable pageable
    ) {
        Pageable validatePageable = PageableUtils.validate(pageable);
        Page<FollowUserResponseDto> followUserResponseDto = followApplicationService.getMyFollowers(
                followeeId, validatePageable);
        return ResponseEntity.ok(ApiResponse.success("내 팔로워 조회 성공", followUserResponseDto));
    }

    //내 팔로워/팔로잉 수 조회
    @RoleGuard(min = "USER")
    @GetMapping("/me/count")
    public ResponseEntity<ApiResponse<FollowCountResponseDto>> getCountMyFollows(
            @RequestHeader("X-User-Id") Long userId
    ) {
        FollowCountResponseDto followCountResponseDto = followApplicationService.getCountMyFollows(
                userId);
        return ResponseEntity.ok(ApiResponse.success("내 팔로워/팔로잉 수 조회 성공", followCountResponseDto));
    }

    //다른 유저 팔로잉 조회
    @RoleGuard(min = "USER")
    @GetMapping("/{targetUserId}/followings")
    public ResponseEntity<ApiResponse<Page<FollowUserResponseDto>>> getFollowings(
            @PathVariable("targetUserId") Long targetUserId,
            @PageableDefault(
                    size = 10,
                    page = 1,
                    sort = {"createdAt", "updatedAt"},
                    direction = Direction.ASC
            ) Pageable pageable
    ) {
        Pageable validatePageable = PageableUtils.validate(pageable);
        Page<FollowUserResponseDto> followUserResponseDto = followApplicationService.getFollowings(
                targetUserId, validatePageable);
        return ResponseEntity.ok(ApiResponse.success("다른 유저 팔로잉 조회 성공", followUserResponseDto));
    }

    //다른 유저 팔로워 조회
    @RoleGuard(min = "USER")
    @GetMapping("/{targetUserId}/followers")
    public ResponseEntity<ApiResponse<Page<FollowUserResponseDto>>> getFollowers(
            @PathVariable("targetUserId") Long targetUserId,
            @PageableDefault(
                    size = 10,
                    page = 1,
                    sort = {"createdAt", "updatedAt"},
                    direction = Direction.ASC
            ) Pageable pageable
    ) {
        Pageable validatePageable = PageableUtils.validate(pageable);
        Page<FollowUserResponseDto> followUserResponseDto = followApplicationService.getFollowers(
                targetUserId, validatePageable);
        return ResponseEntity.ok(ApiResponse.success("다른 유저 팔로워 조회 성공", followUserResponseDto));
    }

    //다른 유저 팔로워/팔로잉 수 조회
    @RoleGuard(min = "USER")
    @GetMapping("/{targetUserId}/count")
    public ResponseEntity<ApiResponse<FollowCountResponseDto>> getCountFollowers(
            @PathVariable("targetUserId") Long targetUserId
    ) {
        FollowCountResponseDto followCountResponseDto = followApplicationService.getCountFollowers(
                targetUserId);
        return ResponseEntity.ok(ApiResponse.success("다른 유저 팔로워/팔로잉 수 조회", followCountResponseDto));
    }

    //팔로우 전체 검색 - 어드민
    @RoleGuard(value = "ADMIN")
    @GetMapping("/admin/search")
    public ResponseEntity<ApiResponse<Page<AdminFollowSearchResponseDto>>> searchAllFollows(
            @RequestParam(
                    name = "followId",
                    required = false
            ) UUID followId,
            @RequestParam(
                    name = "followerId",
                    required = false
            ) Long followerId,
            @RequestParam(
                    name = "followeeId",
                    required = false
            ) Long followeeId,
            @RequestParam(
                    name = "followStatus",
                    required = false
            ) FollowStatus followStatus,
            @PageableDefault(
                    size = 10,
                    page = 1,
                    sort = {"createdAt", "updatedAt"},
                    direction = Direction.ASC
            ) Pageable pageable
    ) {
        Pageable validatePageable = PageableUtils.validate(pageable);
        Page<AdminFollowSearchResponseDto> adminFollowSearchResponseDto = followApplicationService.searchAllFollows(
                followId, followerId, followeeId, followStatus, validatePageable);
        return ResponseEntity.ok(ApiResponse.success("팔로우 전체 검색 성공", adminFollowSearchResponseDto));
    }

}

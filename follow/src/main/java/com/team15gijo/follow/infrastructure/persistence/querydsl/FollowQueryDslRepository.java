package com.team15gijo.follow.infrastructure.persistence.querydsl;

import com.team15gijo.follow.application.dto.v2.AdminFollowSearchCommand;
import com.team15gijo.follow.presentation.dto.response.v2.AdminFollowSearchResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FollowQueryDslRepository {

    Page<AdminFollowSearchResponseDto> searchAllFollowsForAdmin(AdminFollowSearchCommand adminFollowSearchCommand, Pageable validatePageable);
}

package com.team15gijo.search.application.dto.v1;

import com.team15gijo.search.infrastructure.client.post.PostSearchResponseDto;
import com.team15gijo.search.infrastructure.client.user.UserSearchResponseDto;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

@Data
@Builder
public class SearchResponseDto {
    private Page<UserSearchResponseDto> users;
    private Page<PostSearchResponseDto> posts;
}

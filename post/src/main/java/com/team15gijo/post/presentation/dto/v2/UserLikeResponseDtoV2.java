package com.team15gijo.post.presentation.dto.v2;


import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLikeResponseDtoV2 {
    private long userId;
    private List<UUID> postIds;
}

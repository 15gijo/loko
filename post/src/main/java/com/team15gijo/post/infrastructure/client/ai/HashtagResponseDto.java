package com.team15gijo.post.infrastructure.client.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HashtagResponseDto {
    private List<String> hashtags;
}

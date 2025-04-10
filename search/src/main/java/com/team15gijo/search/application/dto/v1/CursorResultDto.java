package com.team15gijo.search.application.dto.v1;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CursorResultDto<T> {
    private List<T> items;
    private Object nextCursor;
    private boolean hasNext;
}

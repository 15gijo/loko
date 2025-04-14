package com.team15gijo.common.utils.page;

import java.util.EnumSet;
import lombok.Getter;

@Getter
public enum PageSize {
    DEFAULT(10),
    THIRTY(30),
    FIFTY(50);

    private int pageSize;

    PageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public static boolean isValidSize(int pageSize) {
        return EnumSet.allOf(PageSize.class)
                .stream()
                .anyMatch(ps -> ps.getPageSize() == pageSize);
    }


}

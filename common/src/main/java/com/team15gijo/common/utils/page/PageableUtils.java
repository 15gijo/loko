package com.team15gijo.common.utils.page;

import com.team15gijo.common.exception.CommonExceptionCode;
import com.team15gijo.common.exception.CustomException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PageableUtils {

    private static final int FIRST_PAGE_NUMBER = 1;

    public static Pageable validate(Pageable pageable) {

        validatePageNumber(pageable.getPageNumber());
        validatePageSize(pageable.getPageSize());

        return PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize(), pageable.getSort());

    }

    private static void validatePageNumber(int pageNumber) {
        if (pageNumber < FIRST_PAGE_NUMBER) {
            throw new CustomException(CommonExceptionCode.INVALID_PAGE_NUMBER);
        }
    }

    private static void validatePageSize(int pageSize) {
        if (!PageSize.isValidSize(pageSize)) {
            throw new CustomException(CommonExceptionCode.INVALID_PAGE_SIZE);
        }
    }
}

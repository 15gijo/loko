// src/main/java/com/team15gijo/comment/application/filter/CommentFilter.java

package com.team15gijo.comment.application.filter.v2;

import com.team15gijo.comment.application.config.v2.CommentProperties;
import com.team15gijo.comment.domain.exception.CommentDomainException;
import com.team15gijo.comment.domain.exception.CommentDomainExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommentFilter {


    private final CommentProperties props;

    /**
     * 금지어가 포함되어 있으면 예외 발생
     */
    public void validateContent(String content) {
        String lowered = content.toLowerCase();
        for (String word : props.getBlacklist()) {
            if (lowered.contains(word.toLowerCase())) {
                throw new CommentDomainException(CommentDomainExceptionCode.PROHIBITED_WORD);
            }
        }
    }
}

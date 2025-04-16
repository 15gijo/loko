package com.team15gijo.comment.domain.repository.v1;

import com.team15gijo.comment.domain.model.v1.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    // 특정 게시글에 속한 댓글 목록 조회 (페이징 지원)
    Page<Comment> findByPostId(UUID postId, Pageable pageable);
}

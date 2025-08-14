package com.moments.backend.repositories;

import com.moments.backend.entities.Comment;
import com.moments.backend.entities.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    Page<Comment> findByVideoAndParentCommentIsNullOrderByCommentDateAsc(Video video, Pageable pageable);
    List<Comment> findByParentCommentOrderByCommentDateAsc(Comment parentComment);
}
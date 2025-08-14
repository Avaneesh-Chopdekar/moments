package com.moments.backend.services;

import com.moments.backend.dto.CommentCreateDto;
import com.moments.backend.dto.CommentUpdateDto;
import com.moments.backend.entities.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface CommentService {
    Comment addCommentToVideo(UUID videoId, UUID userId, CommentCreateDto commentCreateDto);

    Comment addReplyToComment(UUID parentCommentId, UUID userId, CommentCreateDto commentCreateDto);

    Comment getCommentById(UUID commentId);

    Page<Comment> getTopLevelCommentsForVideo(UUID videoId, Pageable pageable);

    List<Comment> getRepliesForComment(UUID commentId);

    Comment updateComment(UUID commentId, CommentUpdateDto commentUpdateDto, UUID currentUserId);

    void deleteComment(UUID commentId, UUID currentUserId); // User can delete own, Video owner/Admin can delete any on their video
}
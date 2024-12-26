package ru.practicum.ewm.comment.service;

import ru.practicum.ewm.comment.dto.CommentRequestDto;
import ru.practicum.ewm.comment.dto.CommentResponseDto;

import java.util.List;

public interface  CommentService {
    CommentResponseDto createComment(Long userId, Long eventId, CommentRequestDto commentDto);

    CommentResponseDto updateComment(Long userId, Long commentId, CommentRequestDto updateCommentDto);

    List<CommentResponseDto> getCommentsByUser(Long userId);

    CommentResponseDto getCommentByUserAndCommentId(Long userId, Long commentId);

    List<CommentResponseDto> getCommentsByEvent(Long eventId, Integer from, Integer size);

    void deleteComment(Long userId, Long commentId);

    void deleteAdmin(Long commentId);

    List<CommentResponseDto> searchComment(String text, Integer from, Integer size);
}
package ru.practicum.ewm.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentRequestDto;
import ru.practicum.ewm.comment.dto.CommentResponseDto;
import ru.practicum.ewm.comment.service.CommentService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/comments")
public class CommentPrivateController {
    private final CommentService commentService;
    public static final String ENDPOINT_PATH = "/users/{userId}/{commentId}";

    @PostMapping("/users/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto createComment(@PathVariable Long userId,
                                            @PathVariable Long eventId,
                                            @Valid @RequestBody CommentRequestDto commentRequestDto) {
        log.info("Добавление коментария: {} к событию = {} от пользовталея = {} ", commentRequestDto.toString(), eventId, userId);
        return commentService.createComment(userId, eventId, commentRequestDto);
    }

    @PatchMapping(ENDPOINT_PATH)
    public CommentResponseDto updateComment(@PathVariable Long userId, @PathVariable Long commentId,
                                            @Valid @RequestBody CommentRequestDto updateCommentDto) {

        log.info("Обновление пользователем с userId = {}  коментария с commentId = {} ", userId, commentId);
        return commentService.updateComment(userId, commentId, updateCommentDto);
    }

    @GetMapping("/users/{userId}/comments")
    public List<CommentResponseDto> getCommentByUser(@PathVariable Long userId) {
        log.info("Получение коментариев пользователя с userId = {} ", userId);
        return commentService.getCommentsByUser(userId);
    }

    @DeleteMapping(ENDPOINT_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId, @PathVariable Long commentId) {
        log.info("Удаление коментария id = {} пользователем id = {} ", userId, commentId);
        commentService.deleteComment(userId, commentId);
    }

    @GetMapping(ENDPOINT_PATH)
    public CommentResponseDto getComment(@PathVariable Long userId, @PathVariable Long commentId) {
        log.info("Получения коментария id = {} пользователем id = {} ", commentId, userId);
        return commentService.getCommentByUserAndCommentId(userId, commentId);
    }
}
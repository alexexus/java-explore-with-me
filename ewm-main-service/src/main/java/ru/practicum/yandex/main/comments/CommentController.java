package ru.practicum.yandex.main.comments;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.yandex.main.comments.dto.CommentDto;
import ru.practicum.yandex.main.comments.dto.NewCommentDto;
import ru.practicum.yandex.main.comments.dto.UpdateCommentRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping
@RequiredArgsConstructor
public class CommentController {

    private final CommentService service;
    private final CommentMapper mapper;

    @PostMapping("/users/{userId}/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@PathVariable long userId,
                                 @PathVariable long eventId,
                                 @RequestBody @Valid NewCommentDto comment) {
        return mapper.toCommentDto(service.addComment(userId, eventId, mapper.toComment(comment)));
    }

    @GetMapping("/comments/{commentId}")
    public CommentDto getCommentById(@PathVariable long commentId) {
        return mapper.toCommentDto(service.getCommentById(commentId));
    }

    @PatchMapping("/users/{userId}/events/{eventId}/comments/{commentId}")
    public CommentDto updateComment(@PathVariable long commentId,
                                    @PathVariable long userId,
                                    @PathVariable long eventId,
                                    @RequestBody @Valid UpdateCommentRequest request) {
        return mapper.toCommentDto(service.updateComment(commentId, userId, eventId, request));
    }

    @DeleteMapping("/users/{userId}/events/{eventId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long commentId,
                              @PathVariable long userId,
                              @PathVariable long eventId) {
        service.deleteComment(commentId, userId, eventId);
    }

    @DeleteMapping("/admin/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long commentId) {
        service.deleteCommentByAdmin(commentId);
    }

    @GetMapping("/events/comments/{eventId}")
    public List<CommentDto> getAllCommentsByEventId(@PathVariable long eventId,
                                                    @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                    @Positive @RequestParam(defaultValue = "10") Integer size) {
        return service.getAllCommentsByEventId(eventId, from, size).stream()
                .map(mapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/users/comments/{userId}")
    public List<CommentDto> getAllCommentsByAuthorId(@PathVariable long userId,
                                                     @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(defaultValue = "10") Integer size) {
        return service.getAllCommentsByAuthorId(userId, from, size).stream()
                .map(mapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/comments")
    public List<CommentDto> getAllCommentsByText(@RequestParam String text,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(defaultValue = "10") Integer size) {
        return service.getCommentsByText(text, from, size).stream()
                .map(mapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/comments/all")
    public List<CommentDto> getAllComments(@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                           @Positive @RequestParam(defaultValue = "10") Integer size) {
        return service.getAllComments(from, size).stream()
                .map(mapper::toCommentDto)
                .collect(Collectors.toList());
    }
}

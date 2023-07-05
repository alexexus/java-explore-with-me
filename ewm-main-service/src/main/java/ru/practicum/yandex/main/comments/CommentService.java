package ru.practicum.yandex.main.comments;

import ru.practicum.yandex.main.comments.dto.UpdateCommentRequest;

import java.util.List;

public interface CommentService {

    Comment addComment(long userId, long eventId, Comment comment);

    Comment getCommentById(long commentId);

    Comment updateComment(long commentId, long userId, long eventId, UpdateCommentRequest request);

    void deleteComment(long commentId, long userId, long eventId);

    void deleteCommentByAdmin(long commentId);

    List<Comment> getAllCommentsByEventId(long eventId, Integer from, Integer size);

    List<Comment> getAllCommentsByAuthorId(long authorId, Integer from, Integer size);

    List<Comment> getCommentsByText(String text, Integer from, Integer size);

    List<Comment> getAllComments(Integer from, Integer size);
}

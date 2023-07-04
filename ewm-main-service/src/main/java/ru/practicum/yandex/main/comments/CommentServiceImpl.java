package ru.practicum.yandex.main.comments;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.yandex.main.comments.dto.UpdateCommentRequest;
import ru.practicum.yandex.main.events.EventRepository;
import ru.practicum.yandex.main.exception.NotFoundException;
import ru.practicum.yandex.main.exception.ValidationException;
import ru.practicum.yandex.main.users.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentRepository repository;

    @Override
    public Comment addComment(long userId, long eventId, Comment comment) {
        comment.setAuthor(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found")));
        comment.setEvent(eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found")));
        comment.setCreated(LocalDateTime.now());

        return repository.save(comment);
    }

    @Override
    public Comment getCommentById(long commentId) {
        return repository.findById(commentId).orElseThrow(() -> new NotFoundException("Comment not found"));
    }

    @Override
    public Comment updateComment(long commentId, long userId, long eventId, UpdateCommentRequest request) {
        Comment oldComment = getCommentById(commentId);
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));

        if (!oldComment.getEditable()) {
            throw new ValidationException("Comment cannot be edited");
        }
        if (oldComment.getAuthor().getId() != userId) {
            throw new ValidationException("User is not author");
        }
        if (request.getText() != null && !request.getText().isBlank()) {
            oldComment.setText(request.getText());
        }

        return repository.save(oldComment);
    }

    @Override
    public void deleteComment(long commentId, long userId, long eventId) {
        Comment comment = getCommentById(commentId);
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));

        if (comment.getAuthor().getId() != userId) {
            throw new ValidationException("User is not author");
        }

        repository.deleteById(commentId);
    }

    @Override
    public List<Comment> getAllCommentsByEventId(long eventId, Integer from, Integer size) {
        return repository.findByEventId(eventId, PageRequest.of(from / size, size));
    }

    @Override
    public List<Comment> getAllCommentsByAuthorId(long authorId, Integer from, Integer size) {
        return repository.findByAuthorId(authorId, PageRequest.of(from / size, size));
    }

    @Override
    public List<Comment> getCommentsByText(String text, Integer from, Integer size) {
        return repository.findByTextIgnoreCase(text, PageRequest.of(from / size, size));
    }

    @Override
    public List<Comment> getAllComments(Integer from, Integer size) {
        return repository.findAll(PageRequest.of(from / size, size)).toList();
    }
}

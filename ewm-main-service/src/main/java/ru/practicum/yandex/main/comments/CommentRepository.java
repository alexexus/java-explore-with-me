package ru.practicum.yandex.main.comments;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByTextIgnoreCase(String text, Pageable pageable);

    List<Comment> findByEventId(long eventId, Pageable pageable);

    List<Comment> findByAuthorId(long authorId, Pageable pageable);
}

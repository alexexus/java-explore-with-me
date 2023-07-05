package ru.practicum.yandex.main.comments;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.yandex.main.comments.dto.EventsComments;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByTextIgnoreCase(String text, Pageable pageable);

    List<Comment> findByEventId(long eventId, Pageable pageable);

    List<Comment> findByAuthorId(long authorId, Pageable pageable);

    @Query("select new ru.practicum.yandex.main.comments.dto.EventsComments(c.event.id, count(c.id)) " +
            "from Comment as c where c.event.id in :eventsIds group by c.event.id")
    List<EventsComments> getEventsComments(List<Long> eventsIds);
}

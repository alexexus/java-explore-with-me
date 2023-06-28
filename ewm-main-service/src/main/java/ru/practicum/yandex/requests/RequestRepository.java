package ru.practicum.yandex.requests;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.yandex.events.Event;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByEventInitiatorIdAndEventId(long initiatorId, long eventId);

    List<Request> findByIdInAndEventInitiatorIdAndEventId(List<Long> ids, long initiatorId, long eventId);

    List<Request> findByRequesterId(long requesterIid);

    Request findByIdAndRequesterId(long id, long requesterId);

    List<Request> findByEventAndStatusIs(Event event, EventRequestStatus status);

    List<Request> findByRequesterIdAndEventId(long requesterId, long eventId);
}

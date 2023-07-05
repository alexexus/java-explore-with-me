package ru.practicum.yandex.main.requests;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.yandex.main.requests.dto.EventsRequests;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByEventInitiatorIdAndEventId(long initiatorId, long eventId);

    List<Request> findByIdInAndEventInitiatorIdAndEventId(List<Long> ids, long initiatorId, long eventId);

    List<Request> findByRequesterId(long requesterIid);

    Request findByIdAndRequesterId(long id, long requesterId);

    List<Request> findByEventIdAndStatusIs(long eventId, EventRequestStatus status);

    List<Request> findByRequesterIdAndEventId(long requesterId, long eventId);

    @Query("select new ru.practicum.yandex.main.requests.dto.EventsRequests(r.event.id, count(r.id)) " +
            "from Request as r where r.event.id in :eventsIds and r.status = 'CONFIRMED' group by r.event.id")
    List<EventsRequests> getEventsRequests(List<Long> eventsIds);
}

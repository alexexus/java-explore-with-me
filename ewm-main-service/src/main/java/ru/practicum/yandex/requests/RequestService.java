package ru.practicum.yandex.requests;

import ru.practicum.yandex.requests.dto.EventRequestStatusUpdateRequest;
import ru.practicum.yandex.requests.dto.EventRequestStatusUpdateResult;

import java.util.List;

public interface RequestService {

    List<Request> findByRequesterIdAndEventId(long requesterId, long eventId);

    EventRequestStatusUpdateResult updateRequests(long userId, long eventId, EventRequestStatusUpdateRequest request);

    List<Request> findByRequesterId(long requesterId);

    Request addRequest(long userId, long eventId);

    Request cancelRequest(long id, long requesterId);
}

package ru.practicum.yandex.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.yandex.client.StatsService;
import ru.practicum.yandex.events.Event;
import ru.practicum.yandex.events.EventRepository;
import ru.practicum.yandex.events.State;
import ru.practicum.yandex.exception.ConflictException;
import ru.practicum.yandex.exception.NotFoundException;
import ru.practicum.yandex.requests.dto.EventRequestStatusUpdateRequest;
import ru.practicum.yandex.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.yandex.users.User;
import ru.practicum.yandex.users.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository repository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestMapper mapper;
    private final StatsService stats;

    @Override
    public List<Request> findByRequesterIdAndEventId(long requesterId, long eventId) {
        userRepository.findById(requesterId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        return repository.findByEventInitiatorIdAndEventId(requesterId, eventId);
    }

    @Override
    public EventRequestStatusUpdateResult updateRequests(long userId, long eventId,
                                                         EventRequestStatusUpdateRequest request) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        List<Request> requests = repository.findByIdInAndEventInitiatorIdAndEventId(request.getRequestIds(),
                userId, eventId);
        EventRequestStatusUpdateResult result = EventRequestStatusUpdateResult.builder()
                .confirmedRequests(new ArrayList<>())
                .rejectedRequests(new ArrayList<>())
                .build();

        if (!requests.stream()
                .map(Request::getStatus)
                .allMatch(EventRequestStatus.PENDING::equals)) {
            throw new ConflictException("Only pending orders can be modified");
        }
        if (request.getStatus().equals(EventRequestStatus.REJECTED)) {
            result.setRejectedRequests(changeStatus(requests, EventRequestStatus.REJECTED).stream()
                    .map(mapper::toRequestDto)
                    .collect(Collectors.toList()));
        } else {
            checkLimit(stats.getConfirmedRequests(List.of(event)).getOrDefault(eventId, 0L) +
                    request.getRequestIds().size(), event.getParticipantLimit());
            result.setConfirmedRequests(changeStatus(requests, EventRequestStatus.CONFIRMED).stream()
                    .map(mapper::toRequestDto)
                    .collect(Collectors.toList()));
            if (stats.getConfirmedRequests(List.of(event)).getOrDefault(eventId, 0L)
                    + request.getRequestIds().size() >= event.getParticipantLimit()) {
                result.setRejectedRequests(changeStatus(repository.findByEventIdAndStatusIs(eventId,
                        EventRequestStatus.PENDING), EventRequestStatus.REJECTED).stream()
                        .map(mapper::toRequestDto)
                        .collect(Collectors.toList()));
            }
        }
        return result;
    }

    @Override
    public List<Request> findByRequesterId(long requesterId) {
        userRepository.findById(requesterId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return repository.findByRequesterId(requesterId);
    }

    @Override
    public Request addRequest(long userId, long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        if (!repository.findByRequesterIdAndEventId(userId, eventId).isEmpty()) {
            throw new ConflictException("You can't add a repeat request");
        }
        if (event.getInitiator().getId() == userId) {
            throw new ConflictException("The event initiator cannot add a request to participate in his event");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("You can't participate in an unpublished event");
        }
        checkLimit(stats.getConfirmedRequests(List.of(event)).getOrDefault(event.getId(), 0L) + 1L,
                event.getParticipantLimit());

        Request request = Request.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user)
                .build();
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(EventRequestStatus.CONFIRMED);
        } else {
            request.setStatus(EventRequestStatus.PENDING);
        }
        return repository.save(request);
    }

    @Override
    public Request cancelRequest(long id, long requesterId) {
        repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Request not found"));
        userRepository.findById(requesterId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Request request = repository.findByIdAndRequesterId(id, requesterId);
        request.setStatus(EventRequestStatus.CANCELED);
        return repository.save(request);
    }

    private void checkLimit(Long newLimit, Long oldLimit) {
        if (oldLimit != 0 && (newLimit > oldLimit)) {
            throw new ConflictException("The event has reached the limit of requests for participation");
        }
    }

    private List<Request> changeStatus(List<Request> requests, EventRequestStatus status) {
        requests.forEach(request -> request.setStatus(status));
        return repository.saveAll(requests);
    }
}

package ru.practicum.yandex.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository repository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestMapper mapper;

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
        eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        List<Request> requests = repository.findByIdInAndEventInitiatorIdAndEventId(request.getRequestIds(),
                userId, eventId);
        EventRequestStatusUpdateResult result = EventRequestStatusUpdateResult.builder()
                .confirmedRequests(new ArrayList<>())
                .rejectedRequests(new ArrayList<>())
                .build();
        for (Request req : requests) {
            if (req.getEvent().getParticipantLimit() <= req.getEvent().getConfirmedRequests()) {
                throw new ConflictException("The limit on applications for this event has been reached");
            }
            if (!req.getStatus().equals(EventRequestStatus.PENDING)) {
                throw new ConflictException("Status must be PENDING");
            }
            if (request.getStatus().equals(EventRequestStatus.CONFIRMED)) {
                req.setStatus(EventRequestStatus.CONFIRMED);
                req.getEvent().setConfirmedRequests(req.getEvent().getConfirmedRequests() + 1L);
                result.getConfirmedRequests().add(mapper.toRequestDto(req));
                eventRepository.save(req.getEvent());
                repository.save(req);
            } else {
                req.setStatus(EventRequestStatus.REJECTED);
                result.getRejectedRequests().add(mapper.toRequestDto(req));
                repository.save(req);
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
        if (event.getParticipantLimit() < event.getConfirmedRequests() + 1 && event.getParticipantLimit() != 0) {
            throw new ConflictException("The event has reached the limit of requests for participation");
        }
        Request request = Request.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user)
                .status(EventRequestStatus.CONFIRMED)
                .build();
        if (event.getRequestModeration() && event.getParticipantLimit() != 0) {
            request.setStatus(EventRequestStatus.PENDING);
        }
        if (request.getStatus().equals(EventRequestStatus.CONFIRMED)) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
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
}

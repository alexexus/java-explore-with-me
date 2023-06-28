package ru.practicum.yandex.events;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.yandex.categories.CategoryRepository;
import ru.practicum.yandex.client.StatsService;
import ru.practicum.yandex.events.dto.UpdateEventRequest;
import ru.practicum.yandex.events.location.LocationRepository;
import ru.practicum.yandex.exception.ConflictException;
import ru.practicum.yandex.exception.NotFoundException;
import ru.practicum.yandex.exception.ValidationException;
import ru.practicum.yandex.requests.EventRequestStatus;
import ru.practicum.yandex.requests.RequestRepository;
import ru.practicum.yandex.users.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final EventRepository repository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final StatsService hitClient;

    @Override
    public List<Event> getAllEvents(List<Long> users, List<State> states, List<Long> categories,
                                    String rangeStart, String rangeEnd, Integer from, Integer size) {
        QEvent event = QEvent.event;
        List<BooleanExpression> options = new ArrayList<>();
        if (users != null) {
            options.add(event.initiator.id.in(users));
        }
        if (states != null) {
            options.add(event.state.in(states));
        }
        if (categories != null) {
            options.add(event.category.id.in(categories));
        }
        getTimeOptions(rangeStart, rangeEnd, event, options);

        BooleanExpression expression = options.stream()
                .reduce(BooleanExpression::and)
                .get();
        List<Event> events = repository.findAll(expression, PageRequest.of(from / size, size)).toList();
        events.forEach(e -> e.setConfirmedRequests(requestRepository
                .findByEventAndStatusIs(e, EventRequestStatus.CONFIRMED).size()));
        return events;
    }

    @Override
    public Event updateEventAdmin(long id, UpdateEventRequest event) {
        Event oldEvent = repository.findById(id).orElseThrow(() -> new NotFoundException("Event not found"));
        if (oldEvent.getState().equals(State.PUBLISHED) || oldEvent.getState().equals(State.CANCELED)) {
            throw new ConflictException("Only pending events can be changed");
        }
        return updateEvent(event, oldEvent);
    }

    @Override
    public List<Event> findByText(String text, List<Long> categories, Boolean paid,
                                  String start, String end, String order, Integer from, Integer size,
                                  Boolean onlyAvailable, HttpServletRequest httpServletRequest) {
        Sort sort = Sort.unsorted();
        if (order != null) {
            if (order.equals("EVENT_DATE")) {
                sort = Sort.by("eventDate");
            }
            if (order.equals("VIEWS")) {
                sort = Sort.by("views");
            }
        }

        QEvent event = QEvent.event;
        List<BooleanExpression> options = new ArrayList<>();
        if (text != null) {
            options.add(event.annotation.like(text).or(event.description.like(text)));
        }
        if (categories != null && !categories.isEmpty()) {
            options.add(event.category.id.in(categories));
        }
        if (paid != null) {
            options.add(event.paid.eq(paid));
        }
        getTimeOptions(start, end, event, options);

        BooleanExpression expression = options.stream()
                .reduce(BooleanExpression::and)
                .get();
        List<Event> events = repository.findAll(expression, PageRequest.of(from / size, size, sort)).toList();

        if (onlyAvailable) {
            events = events.stream()
                    .filter(e -> e.getConfirmedRequests() < e.getParticipantLimit())
                    .collect(Collectors.toList());
        }

        hitClient.addHit(httpServletRequest);
        Map<Long, Long> views = hitClient.getViews(events);
        events = events.stream()
                .peek(e -> e.setViews(views.get(e.getId())))
                .collect(Collectors.toList());

        return events;
    }

    @Override
    public Event getEventByIdAndState(long id, HttpServletRequest httpServletRequest) {
        Event event = repository.findById(id).orElseThrow(() -> new NotFoundException("Event not found"));
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException("Event not published");
        }

        hitClient.addHit(httpServletRequest);
        Map<Long, Long> views = hitClient.getViews(List.of(event));
        event.setViews(views.get(event.getId()));

        return event;
    }

    @Override
    public List<Event> getByInitiatorId(long id, Integer from, Integer size) {
        userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        return repository.findByInitiatorId(id, PageRequest.of(from / size, size));
    }

    @Override
    public Event addEvent(long id, Event event) {
        event.setCategory(categoryRepository.findById(event.getCategory().getId())
                .orElseThrow(() -> new NotFoundException("Category not found")));
        event.setInitiator(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found")));
        event.setLocation(locationRepository.findByLatAndLon(event.getLocation().getLat(), event.getLocation().getLon())
                .orElseGet(() -> locationRepository.save(event.getLocation())));
        if (event.getEventDate().minusHours(2L).isBefore(LocalDateTime.now())) {
            throw new ValidationException("Must contain a date that has not yet arrived");
        }
        return repository.save(event);
    }

    @Override
    public Event getEventByIdAndInitiatorId(long eventId, long initiatorId) {
        userRepository.findById(initiatorId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        repository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        return repository.findByIdAndInitiatorId(eventId, initiatorId);
    }

    @Override
    public Event updateEventUser(long initiatorId, long eventId, UpdateEventRequest event) {
        Event oldEvent = getEventByIdAndInitiatorId(eventId, initiatorId);
        if (oldEvent.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }
        return updateEvent(event, oldEvent);
    }

    private Event updateEvent(UpdateEventRequest event, Event oldEvent) {
        if (event.getAnnotation() != null && !event.getAnnotation().isBlank()) {
            oldEvent.setAnnotation(event.getAnnotation());
        }
        if (event.getCategory() != null) {
            oldEvent.setCategory(categoryRepository.findById(event.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category not found")));
        }
        if (event.getDescription() != null && !event.getDescription().isBlank()) {
            oldEvent.setDescription(event.getDescription());
        }
        if (event.getEventDate() != null) {
            if (getTime(event.getEventDate()).minusHours(2L).isBefore(LocalDateTime.now())) {
                throw new ValidationException("Must contain a date that has not yet arrived");
            }
            oldEvent.setEventDate(getTime(event.getEventDate()));
        }
        if (event.getLocation() != null) {
            oldEvent.setLocation(locationRepository.findByLatAndLon(event.getLocation().getLat(),
                    event.getLocation().getLon()).orElseGet(() -> locationRepository.save(event.getLocation())));
        }
        if (event.getPaid() != null) {
            oldEvent.setPaid(event.getPaid());
        }
        if (event.getParticipantLimit() != null) {
            oldEvent.setParticipantLimit(event.getParticipantLimit());
        }
        if (event.getRequestModeration() != null) {
            oldEvent.setRequestModeration(event.getRequestModeration());
        }
        if (event.getStateAction() != null) {
            switch (event.getStateAction()) {
                case SEND_TO_REVIEW:
                    oldEvent.setState(State.PENDING);
                    break;
                case CANCEL_REVIEW:
                case REJECT_EVENT:
                    oldEvent.setState(State.CANCELED);
                    break;
                case PUBLISH_EVENT:
                    oldEvent.setState(State.PUBLISHED);
                    oldEvent.setPublishedOn(LocalDateTime.now());
                    if (oldEvent.getEventDate().isBefore(oldEvent.getPublishedOn().plusHours(1L))) {
                        throw new ValidationException("The date of the event must be no earlier than one hour from " +
                                "the date of publication");
                    }
                    break;
            }
        }
        if (event.getTitle() != null) {
            oldEvent.setTitle(event.getTitle());
        }
        return repository.save(oldEvent);
    }

    private LocalDateTime getTime(String time) {
        return LocalDateTime.parse(time, DATE);
    }

    private void getTimeOptions(String start, String end, QEvent event, List<BooleanExpression> options) {
        if (start != null) {
            options.add(event.eventDate.after(getTime(start)));
        }
        if (end != null) {
            options.add(event.eventDate.before(getTime(end)));
        }
        if (start == null && end == null) {
            options.add(event.eventDate.after(LocalDateTime.now()));
        }
        if (start != null && end != null && getTime(start).isAfter(getTime(end))) {
            throw new ValidationException("Start after end");
        }
    }
}

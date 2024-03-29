package ru.practicum.yandex.main.events;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.yandex.main.events.dto.EventFullDto;
import ru.practicum.yandex.main.events.dto.EventShortDto;
import ru.practicum.yandex.main.events.dto.NewEventDto;
import ru.practicum.yandex.main.events.dto.UpdateEventRequest;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping
@RequiredArgsConstructor
public class EventController {

    private final EventService service;
    private final EventMapper mapper;

    @GetMapping("/admin/events")
    public List<EventFullDto> getAllEvents(@RequestParam(required = false) List<Long> users,
                                           @RequestParam(required = false) List<State> states,
                                           @RequestParam(required = false) List<Long> categories,
                                           @RequestParam(required = false)
                                           @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                           @RequestParam(required = false)
                                           @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                           @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                           @Positive @RequestParam(defaultValue = "10") Integer size) {
        return service.getAllEvents(users, states, categories, rangeStart, rangeEnd, from, size).stream()
                .map(mapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/admin/events/{eventId}")
    public EventFullDto updateEventAdmin(@PathVariable long eventId,
                                         @RequestBody @Valid UpdateEventRequest event) {
        return mapper.toEventFullDto(service.updateEventAdmin(eventId, event));
    }

    @GetMapping("/events")
    public List<EventShortDto> getEventsByText(@RequestParam(required = false) String text,
                                               @RequestParam(required = false) List<Long> categories,
                                               @RequestParam(required = false) Boolean paid,
                                               @RequestParam(required = false)
                                               @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                               @RequestParam(required = false)
                                               @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                               @RequestParam(required = false, defaultValue = "false")
                                               Boolean onlyAvailable,
                                               @RequestParam(required = false) String sort,
                                               @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                               @Positive @RequestParam(defaultValue = "10") Integer size,
                                               HttpServletRequest httpServletRequest) {
        return service.findByText(text, categories, paid, rangeStart, rangeEnd, sort, from, size, onlyAvailable,
                        httpServletRequest).stream()
                .map(mapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/events/{id}")
    public EventFullDto getEventById(@PathVariable long id, HttpServletRequest httpServletRequest) {
        return mapper.toEventFullDto(service.getEventByIdAndState(id, httpServletRequest));
    }

    @GetMapping("/users/{userId}/events")
    public List<EventShortDto> getEventsByInitiatorId(@PathVariable long userId,
                                                      @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                      @Positive @RequestParam(defaultValue = "10") Integer size) {
        return service.getByInitiatorId(userId, from, size).stream()
                .map(mapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable long userId,
                                 @RequestBody @Valid NewEventDto newEventDto) {
        return mapper.toEventFullDto(service.addEvent(userId, mapper.toEvent(newEventDto)));
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventFullDto getEventByIdAndInitiatorId(@PathVariable long userId,
                                                   @PathVariable long eventId) {
        return mapper.toEventFullDto(service.getEventByIdAndInitiatorId(eventId, userId));
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventFullDto updateEventPrivate(@PathVariable long userId,
                                           @PathVariable long eventId,
                                           @RequestBody @Valid UpdateEventRequest event) {
        return mapper.toEventFullDto(service.updateEventUser(userId, eventId, event));
    }
}

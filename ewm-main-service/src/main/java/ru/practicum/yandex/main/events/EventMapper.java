package ru.practicum.yandex.main.events;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.yandex.main.categories.Category;
import ru.practicum.yandex.main.categories.CategoryMapper;
import ru.practicum.yandex.main.events.dto.EventFullDto;
import ru.practicum.yandex.main.events.dto.EventShortDto;
import ru.practicum.yandex.main.users.UserMapper;
import ru.practicum.yandex.main.events.dto.NewEventDto;
import ru.practicum.yandex.main.events.location.LocationMapper;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class EventMapper {

    private final CategoryMapper categoryMapper;
    private final LocationMapper locationMapper;
    private final UserMapper userMapper;

    public EventFullDto toEventFullDto(Event event) {
        LocalDateTime publishedOn = null;
        if (event.getPublishedOn() != null) {
            publishedOn = event.getPublishedOn();
        }
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(categoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(userMapper.toUserShortDto(event.getInitiator()))
                .location(locationMapper.toLocationDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(publishedOn)
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .comments(event.getComments())
                .build();
    }

    public EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(categoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate())
                .initiator(userMapper.toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .comments(event.getComments())
                .build();
    }

    public Event toEvent(NewEventDto event) {
        return Event.builder()
                .annotation(event.getAnnotation())
                .category(Category.builder().id(event.getCategory()).build())
                .confirmedRequests(0L)
                .createdOn(LocalDateTime.now())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .location(locationMapper.toLocation(event.getLocation()))
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(null)
                .requestModeration(event.isRequestModeration())
                .state(State.PENDING)
                .title(event.getTitle())
                .views(0L)
                .comments(0L)
                .build();
    }
}

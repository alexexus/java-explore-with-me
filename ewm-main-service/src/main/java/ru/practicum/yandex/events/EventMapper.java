package ru.practicum.yandex.events;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.yandex.categories.Category;
import ru.practicum.yandex.categories.CategoryMapper;
import ru.practicum.yandex.events.dto.EventFullDto;
import ru.practicum.yandex.events.dto.EventShortDto;
import ru.practicum.yandex.events.dto.NewEventDto;
import ru.practicum.yandex.users.UserMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
@AllArgsConstructor
public class EventMapper {

    public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;

    public EventFullDto toEventFullDto(Event event) {
        String publishedOn = null;
        if (event.getPublishedOn() != null) {
            publishedOn = event.getPublishedOn().format(DATE);
        }
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(categoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn().format(DATE))
                .description(event.getDescription())
                .eventDate(event.getEventDate().format(DATE))
                .initiator(userMapper.toUserShortDto(event.getInitiator()))
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(publishedOn)
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(categoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate().format(DATE))
                .initiator(userMapper.toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public Event toEvent(NewEventDto event) {
        return Event.builder()
                .annotation(event.getAnnotation())
                .category(Category.builder().id(event.getCategory()).build())
                .confirmedRequests(0L)
                .createdOn(LocalDateTime.now())
                .description(event.getDescription())
                .eventDate(LocalDateTime.parse(event.getEventDate(), DATE))
                .location(event.getLocation())
                .paid(Optional.ofNullable(event.getPaid()).orElse(false))
                .participantLimit(Optional.ofNullable(event.getParticipantLimit()).orElse(0L))
                .publishedOn(null)
                .requestModeration(Optional.ofNullable(event.getRequestModeration()).orElse(true))
                .state(State.PENDING)
                .title(event.getTitle())
                .views(0L)
                .build();
    }
}
